//package com.simple_cabinet_medical.Backend.controller;
//
//import com.simple_cabinet_medical.Backend.service.GoogleStorageService;
//import com.simple_cabinet_medical.Backend.service.MdbImport.MdbVersionDetector;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.*;
//import org.springframework.batch.core.explore.JobExplorer;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.batch.item.ExecutionContext;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/import")
//@Slf4j
//public class ImportController {
//
//    private static final String DEFAULT_MDB_PASSWORD = "farouk123456";
//
//    private final JobLauncher jobLauncher;
//    private final JobExplorer jobExplorer;
//    private final Job mdbImportJob;
//    private final MdbVersionDetector versionDetector;
//    private final GoogleStorageService gcsStorageService;
//
//    public ImportController(
//            JobLauncher jobLauncher,
//            JobExplorer jobExplorer,
//            @Qualifier("mdbImportJob") Job mdbImportJob,
//            MdbVersionDetector versionDetector,
//            GoogleStorageService gcsStorageService
//    ) {
//        this.jobLauncher = jobLauncher;
//        this.jobExplorer = jobExplorer;
//        this.mdbImportJob = mdbImportJob;
//        this.versionDetector = versionDetector;
//        this.gcsStorageService = gcsStorageService;
//    }
//
//    @PostMapping("/mdb/upload-url")
//    public ResponseEntity<?> generateUploadUrl(
//            @RequestParam String fileName,
//            @RequestParam(defaultValue = "application/octet-stream") String contentType
//    ) {
//        try {
//            String gcsPath = "mdb-imports/" + UUID.randomUUID() + "-" + fileName;
//            String uploadUrl = gcsStorageService.generateUploadUrlForMdp(gcsPath, contentType);
//
//            return ResponseEntity.ok(Map.of(
//                    "uploadUrl", uploadUrl,
//                    "gcsPath", gcsPath
//            ));
//
//        } catch (Exception e) {
//            log.error("Error generating upload URL", e);
//            return ResponseEntity.internalServerError().body(
//                    Map.of("error", "Unable to generate upload URL")
//            );
//        }
//    }
//
//    @PostMapping("/mdb/import-from-gcs")
//    public ResponseEntity<?> importFromGcs(
//            @RequestParam String gcsPath,
//            @RequestParam Long clientId,
//            @RequestParam(defaultValue = "") String password
//    ) {
//        long startTime = System.currentTimeMillis();
//
//        log.info("========== MDB IMPORT START ==========");
//        log.info("[MDB-IMPORT] clientId={}", clientId);
//        log.info("[MDB-IMPORT] gcsPath={}", gcsPath);
//
//        String mdbPassword = (password != null && !password.isBlank()) ? password : DEFAULT_MDB_PASSWORD;
//        log.info("[MDB-IMPORT] MDB password: {}", password != null && !password.isBlank() ? "provided by user" : "using default");
//
//        try {
//            File tempFile = gcsStorageService.downloadToTempFile(gcsPath, "mdb-import-", ".mdb");
//
//            var version = versionDetector.detectFromContent(tempFile.getAbsolutePath(), mdbPassword);
//            JobParameters params = buildJobParameters(tempFile, mdbPassword, clientId);
//
//            log.info("[MDB-IMPORT] launching Spring Batch job {} ...", mdbImportJob.getName());
//            JobExecution execution = jobLauncher.run(mdbImportJob, params);
//
//            long totalTime = System.currentTimeMillis() - startTime;
//            log.info("[MDB-IMPORT] jobExecutionId={} status={} (endpoint took {} ms)",
//                    execution.getId(), execution.getStatus(), totalTime);
//            log.info("========== MDB IMPORT ACCEPTED ==========");
//
//            // Optional: Clean up blob from GCS bucket since local temp copy is running
//            // gcsStorageService.deleteBlob(gcsPath);
//
//            return ResponseEntity.accepted().body(Map.of(
//                    "jobExecutionId", execution.getId(),
//                    "detectedVersion", version.name(),
//                    "status", execution.getStatus().toString()
//            ));
//
//        } catch (Exception e) {
//            long totalTime = System.currentTimeMillis() - startTime;
//            log.error("========== MDB IMPORT ERROR ==========");
//            log.error("[MDB-IMPORT] clientId={} gcsPath={} elapsedBeforeError={} ms",
//                    clientId, gcsPath, totalTime);
//            log.error("[MDB-IMPORT] full stack trace:", e);
//
//            return ResponseEntity.status(500).body(Map.of(
//                    "error", e.getMessage() != null ? e.getMessage() : "Error while importing the MDB file"
//            ));
//        }
//    }
//
//    @GetMapping("/mdb/status/{jobExecutionId}")
//    public ResponseEntity<?> importStatus(@PathVariable Long jobExecutionId) {
//        JobExecution execution = jobExplorer.getJobExecution(jobExecutionId);
//        if (execution == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        boolean finished = !execution.isRunning();
//
//        Map<String, Object> body = new HashMap<>();
//        body.put("jobExecutionId", jobExecutionId);
//        body.put("status", execution.getStatus().toString());
//        body.put("finished", finished);
//
//        if (finished) {
//            body.put("result", buildResultPayload(execution));
//        }
//
//        return ResponseEntity.ok(body);
//    }
//
//    private JobParameters buildJobParameters(File tempFile, String password, Long clientId) {
//        long runId = System.currentTimeMillis();
//        return new JobParametersBuilder()
//                .addString("filePath", tempFile.getAbsolutePath())
//                .addString("password", password)
//                .addLong("clientId", clientId)
//                .addLong("runId", runId)
//                .toJobParameters();
//    }
//
//    private Map<String, Object> buildResultPayload(JobExecution execution) {
//        int patientsImported = 0, patientsFailed = 0, consultationsImported = 0, treatmentsImported = 0;
//        List<String> errors = new ArrayList<>();
//
//        for (StepExecution step : execution.getStepExecutions()) {
//            ExecutionContext ctx = step.getExecutionContext();
//            patientsImported      += ctx.getInt("patientsImported", 0);
//            patientsFailed        += ctx.getInt("patientsFailed", 0);
//            consultationsImported += ctx.getInt("consultationsImported", 0);
//            treatmentsImported    += ctx.getInt("treatmentsImported", 0);
//
//            @SuppressWarnings("unchecked")
//            List<String> stepErrors = (List<String>) ctx.get("errors");
//            if (stepErrors != null) errors.addAll(stepErrors);
//        }
//
//        if (!execution.getAllFailureExceptions().isEmpty()) {
//            for (Throwable t : execution.getAllFailureExceptions()) {
//                if (t.getMessage() != null) {
//                    errors.add(t.getMessage());
//                }
//            }
//        }
//
//        return Map.of(
//                "jobStatus", execution.getStatus().toString(),
//                "patientsImported", patientsImported,
//                "patientsFailed", patientsFailed,
//                "consultationsImported", consultationsImported,
//                "treatmentsImported", treatmentsImported,
//                "errors", errors
//        );
//    }
//}