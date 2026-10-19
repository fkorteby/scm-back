package com.simple_cabinet_medical.Backend.importation.controller;

import com.simple_cabinet_medical.Backend.service.GoogleStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/import")
@Slf4j
public class ImportController {

    private final JobLauncher jobLauncher;
    private final JobExplorer jobExplorer;
    private final Job importDataJob;
    private final GoogleStorageService gcsStorageService;

    public ImportController(
            @Qualifier("asyncJobLauncher") JobLauncher jobLauncher,
            JobExplorer jobExplorer,
            @Qualifier("importDataJob") Job importDataJob,
            GoogleStorageService gcsStorageService
    ) {
        this.jobLauncher = jobLauncher;
        this.jobExplorer = jobExplorer;
        this.importDataJob = importDataJob;
        this.gcsStorageService = gcsStorageService;
    }

    @PostMapping("/mdb/upload-url")
    public ResponseEntity<?> generateUploadUrl(
            @RequestParam String fileName,
            @RequestParam(defaultValue = "application/octet-stream") String contentType
    ) {
        try {

            String gcsPath =
                    "mdb-imports/"
                            + UUID.randomUUID()
                            + "-"
                            + fileName;

            String uploadUrl =
                    gcsStorageService.generateUploadUrlForMdp(
                            gcsPath,
                            contentType
                    );

            return ResponseEntity.ok(
                    Map.of(
                            "uploadUrl", uploadUrl,
                            "gcsPath", gcsPath
                    )
            );

        } catch (Exception e) {

            log.error(
                    "Error generating signed upload URL",
                    e
            );

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "error",
                                    "Unable to generate upload URL"
                            )
                    );
        }
    }

    @PostMapping("/mdb/import-from-gcs")
    public ResponseEntity<?> importFromGcs(
            @RequestParam String gcsPath,
            @RequestParam Long clientId
    ) {

        try {

            File tempFile =
                    gcsStorageService.downloadToTempFile(
                            gcsPath,
                            "mdb-import-",
                            ".mdb"
                    );

            JobParameters parameters =
                    new JobParametersBuilder()
                            .addString(
                                    "filePath",
                                    tempFile.getAbsolutePath()
                            )
                            .addLong(
                                    "clientId",
                                    clientId
                            )
                            .addLong(
                                    "runId",
                                    System.currentTimeMillis()
                            )
                            .toJobParameters();

            JobExecution execution =
                    jobLauncher.run(
                            importDataJob,
                            parameters
                    );

            log.info(
                    "MDB import started: jobExecutionId={}, clientId={}, gcsPath={}",
                    execution.getId(),
                    clientId,
                    gcsPath
            );

            return ResponseEntity
                    .accepted()
                    .body(
                            Map.of(
                                    "jobExecutionId",
                                    execution.getId(),
                                    "status",
                                    execution.getStatus().toString()
                            )
                    );

        } catch (Exception e) {

            log.error(
                    "Error while starting MDB import from GCS",
                    e
            );

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "error",
                                    e.getMessage() != null
                                            ? e.getMessage()
                                            : "Error while importing MDB file"
                            )
                    );
        }
    }

    @GetMapping("/mdb/status/{jobExecutionId}")
    public ResponseEntity<?> importStatus(
            @PathVariable Long jobExecutionId
    ) {

        JobExecution execution =
                jobExplorer.getJobExecution(jobExecutionId);

        if (execution == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        boolean termine =
                !execution.isRunning();

        String statut =
                execution.getStatus().toString();

        Map<String, Object> body =
                new HashMap<>();

        body.put(
                "jobExecutionId",
                execution.getId()
        );

        body.put(
                "statut",
                statut
        );

        body.put(
                "termine",
                termine
        );

        /*
         * Tant que le job tourne,
         * on retourne simplement son état.
         */
        if (!termine) {
            return ResponseEntity.ok(body);
        }

        long patientsImportes = 0;
        long consultationsImportees = 0;
        long traitementsImportes = 0;

        long patientsLus = 0;
        long consultationsLues = 0;

        long patientsEchoues = 0;
        long consultationsEchouees = 0;

        /*
         * Parcours des steps du job
         */
        for (StepExecution step :
                execution.getStepExecutions()) {

            String stepName =
                    step.getStepName();

            log.info(
                    "Import status - step={}, readCount={}, writeCount={}, " +
                            "filterCount={}, readSkipCount={}, processSkipCount={}, writeSkipCount={}",
                    stepName,
                    step.getReadCount(),
                    step.getWriteCount(),
                    step.getFilterCount(),
                    step.getReadSkipCount(),
                    step.getProcessSkipCount(),
                    step.getWriteSkipCount()
            );

            /*
             * ============================
             * PATIENTS
             * ============================
             */
            if ("importPatientsStep".equals(stepName)) {

                patientsLus =
                        step.getReadCount();

                patientsImportes =
                        step.getWriteCount();

                patientsEchoues =
                        step.getReadSkipCount()
                                + step.getProcessSkipCount()
                                + step.getWriteSkipCount();
            }

            /*
             * ============================
             * CONSULTATIONS
             * ============================
             */
            else if ("importConsultationsStep".equals(stepName)) {

                consultationsLues =
                        step.getReadCount();

                consultationsImportees =
                        step.getWriteCount();

                consultationsEchouees =
                        step.getReadSkipCount()
                                + step.getProcessSkipCount()
                                + step.getWriteSkipCount();

                /*
                 * IMPORTANT :
                 *
                 * Le ConsultationWriter stocke maintenant
                 * le nombre de traitements importés dans
                 * l'ExecutionContext du step.
                 */
                traitementsImportes =
                        step.getExecutionContext()
                                .getLong(
                                        "traitementsImportes",
                                        0L
                                );
            }
        }

        /*
         * ============================
         * ERREURS DU JOB
         * ============================
         */
        List<String> erreurs =
                new ArrayList<>();

        if (!execution
                .getAllFailureExceptions()
                .isEmpty()) {

            for (Throwable t :
                    execution.getAllFailureExceptions()) {

                erreurs.add(
                        t.getMessage() != null
                                ? t.getMessage()
                                : t.toString()
                );
            }
        }

        /*
         * ============================
         * RESULTAT FINAL
         * ============================
         */
        Map<String, Object> resultat =
                new HashMap<>();

        resultat.put(
                "patientsImportes",
                patientsImportes
        );

        resultat.put(
                "patientsEchoues",
                patientsEchoues
        );

        resultat.put(
                "consultationsImportees",
                consultationsImportees
        );

        resultat.put(
                "consultationsEchouees",
                consultationsEchouees
        );

        resultat.put(
                "traitementsImportes",
                traitementsImportes
        );

        resultat.put(
                "erreurs",
                erreurs
        );

        resultat.put(
                "statutJob",
                statut
        );

        resultat.put(
                "patientsLus",
                patientsLus
        );

        resultat.put(
                "consultationsLues",
                consultationsLues
        );

        body.put(
                "resultat",
                resultat
        );

        return ResponseEntity.ok(body);
    }
}