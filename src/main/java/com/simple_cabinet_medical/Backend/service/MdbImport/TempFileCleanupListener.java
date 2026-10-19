package com.simple_cabinet_medical.Backend.service.MdbImport;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Deletes the temporary .mdb file once the Job has REALLY finished (success
 * or failure). Necessary now that the Job runs asynchronously: the file can
 * no longer be deleted right after launching the Job in the controller (as
 * before), since the Job is still reading it at that point in time.
 */
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