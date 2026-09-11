package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.service.MdbImport.MdbVersionDetector;
import com.simple_cabinet_medical.Backend.service.MdbImport.MdbVersionDetector.MdbVersion;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    private final JobLauncher jobLauncher;
    private final JobExplorer jobExplorer;
    private final Job mdbImportJobV17;
    private final Job mdbImportJobV10;
    private final MdbVersionDetector versionDetector;

    public ImportController(JobLauncher jobLauncher,
                            JobExplorer jobExplorer,
                            @Qualifier("mdbImportJobV17") Job mdbImportJobV17,
                            @Qualifier("mdbImportJobV10") Job mdbImportJobV10,
                            MdbVersionDetector versionDetector) {
        this.jobLauncher = jobLauncher;
        this.jobExplorer = jobExplorer;
        this.mdbImportJobV17 = mdbImportJobV17;
        this.mdbImportJobV10 = mdbImportJobV10;
        this.versionDetector = versionDetector;
    }

    /**
     * POST /api/import/mdb
     *
     * Lance le Job en ASYNCHRONE (voir BatchAsyncConfig) et répond
     * IMMÉDIATEMENT avec l'ID du JobExecution — la requête HTTP ne reste plus
     * ouverte pendant toute la durée de l'import (c'était la cause du 502
     * Apache en prod : le reverse proxy coupait avant la fin du Job).
     *
     * Le fichier temporaire n'est PLUS supprimé ici (le Job est encore en
     * train de le lire en arrière-plan) : c'est TempFileCleanupListener qui
     * s'en charge une fois le Job réellement terminé.
     */
    @PostMapping("/mdb")
    public ResponseEntity<?> importMdb(
            @RequestParam("file")            MultipartFile file,
            @RequestParam                    Long          clientId,
            @RequestParam(defaultValue = "") String        password
    ) {
        String mdbPassword = (password != null && !password.isBlank()) ? password : "farouk123456";

        try {
            File temp = File.createTempFile("mdb-import-", ".mdb");
            file.transferTo(temp);

            MdbVersion version = versionDetector.detectFromContent(temp.getAbsolutePath(), mdbPassword);

            Job job = switch (version) {
                case V1_7 -> mdbImportJobV17;
                case V1_0 -> mdbImportJobV10;
            };

            JobParameters params = new JobParametersBuilder()
                    .addString("filePath", temp.getAbsolutePath())
                    .addString("password", mdbPassword)
                    .addLong("clientId", clientId)
                    .addLong("runId", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(job, params); // retourne immédiatement (async)

            return ResponseEntity.accepted().body(Map.of(
                    "jobExecutionId", execution.getId(),
                    "versionDetectee", version.name(),
                    "statut", execution.getStatus().toString()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * GET /api/import/mdb/status/{jobExecutionId}
     *
     * A interroger périodiquement (polling) depuis le frontend tant que
     * "termine" est false. Une fois termine=true, "resultat" contient les
     * mêmes champs qu'avant (patientsImportes, erreurs, etc.).
     */
    @GetMapping("/mdb/status/{jobExecutionId}")
    public ResponseEntity<?> importStatus(@PathVariable Long jobExecutionId) {
        JobExecution execution = jobExplorer.getJobExecution(jobExecutionId);
        if (execution == null) {
            return ResponseEntity.notFound().build();
        }

        boolean termine = !execution.isRunning();

        Map<String, Object> body = new java.util.HashMap<>();
        body.put("jobExecutionId", jobExecutionId);
        body.put("statut", execution.getStatus().toString());
        body.put("termine", termine);

        if (termine) {
            body.put("resultat", buildResultPayload(execution));
        }

        return ResponseEntity.ok(body);
    }

    private Map<String, Object> buildResultPayload(JobExecution execution) {
        int patientsImported = 0, patientsFailed = 0, consultationsImported = 0, traitementsImported = 0;
        List<String> errors = new ArrayList<>();

        for (StepExecution step : execution.getStepExecutions()) {
            ExecutionContext ctx = step.getExecutionContext();
            patientsImported      += ctx.getInt("patientsImported", 0);
            patientsFailed        += ctx.getInt("patientsFailed", 0);
            consultationsImported += ctx.getInt("consultationsImported", 0);
            traitementsImported   += ctx.getInt("traitementsImported", 0);
            @SuppressWarnings("unchecked")
            List<String> stepErrors = (List<String>) ctx.get("errors");
            if (stepErrors != null) errors.addAll(stepErrors);
        }

        return Map.of(
                "statutJob", execution.getStatus().toString(),
                "patientsImportes", patientsImported,
                "patientsEchoues", patientsFailed,
                "consultationsImportees", consultationsImported,
                "traitementsImportes", traitementsImported,
                "erreurs", errors
        );
    }
}