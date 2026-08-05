package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.service.MdbImport.MdbVersionDetector;
import com.simple_cabinet_medical.Backend.service.MdbImport.MdbVersionDetector.MdbVersion;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    private final JobLauncher jobLauncher;
    private final Job mdbImportJobV17;
    private final Job mdbImportJobV10;
    private final MdbVersionDetector versionDetector;

    public ImportController(JobLauncher jobLauncher,
                            @Qualifier("mdbImportJobV17") Job mdbImportJobV17,
                            @Qualifier("mdbImportJobV10") Job mdbImportJobV10,
                            MdbVersionDetector versionDetector) {
        this.jobLauncher = jobLauncher;
        this.mdbImportJobV17 = mdbImportJobV17;
        this.mdbImportJobV10 = mdbImportJobV10;
        this.versionDetector = versionDetector;
    }

    /**
     * POST /api/import/mdb
     *
     * Détecte automatiquement la version depuis le contenu du fichier, puis
     * lance le Job Spring Batch correspondant (streaming, adapté aux gros
     * volumes). Le fichier temporaire doit rester présent pendant TOUTE la
     * durée du Job (le reader garde une connexion JDBC dessus) : on ne le
     * supprime qu'après jobLauncher.run(...) qui est synchrone (bloque
     * jusqu'à la fin du Job avec le JobLauncher par défaut de Spring Boot).
     */
    @PostMapping("/mdb")
    public ResponseEntity<?> importMdb(
            @RequestParam("file")            MultipartFile file,
            @RequestParam                    Long          clientId,
            @RequestParam(defaultValue = "") String        password
    ) {
        File temp = null;
        String mdbPassword = (password != null && !password.isBlank()) ? password : "farouk123456";

        try {
            temp = File.createTempFile("mdb-import-", ".mdb");
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
                    // paramètre technique pour que Spring Batch considère chaque
                    // import comme une nouvelle exécution de Job (sinon, mêmes
                    // paramètres = mêmes fichiers réimportés = "JobInstanceAlreadyCompleteException")
                    .addLong("runId", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(job, params);

            return ResponseEntity.ok(buildResponse(version, execution));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("erreur", e.getMessage()));
        } finally {
            if (temp != null) {
                try { Files.deleteIfExists(temp.toPath()); } catch (Exception ignored) {}
            }
        }
    }

    private Map<String, Object> buildResponse(MdbVersion version, JobExecution execution) {
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
                "versionDetectee", version.name(),
                "statutJob", execution.getStatus().toString(),
                "patientsImportes", patientsImported,
                "patientsEchoues", patientsFailed,
                "consultationsImportees", consultationsImported,
                "traitementsImportes", traitementsImported,
                "erreurs", errors
        );
    }
}