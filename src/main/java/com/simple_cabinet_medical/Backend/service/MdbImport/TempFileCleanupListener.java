package com.simple_cabinet_medical.Backend.service.MdbImport;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TempFileCleanupListener implements JobExecutionListener {

    @Override
    public void afterJob(JobExecution jobExecution) {
        String filePath = jobExecution.getJobParameters().getString("filePath");
        if (filePath == null) return;

        try {
            Files.deleteIfExists(Path.of(filePath));
        } catch (IOException ignored) {
            // Non-blocking: worst case, an orphaned temp file in /tmp.
        }
    }
}