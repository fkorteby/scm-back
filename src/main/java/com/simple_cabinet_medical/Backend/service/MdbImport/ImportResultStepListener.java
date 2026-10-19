package com.simple_cabinet_medical.Backend.service.MdbImport;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;

import java.util.ArrayList;

/**
 * At the end of the step, copies the counters and error messages
 * accumulated by the writer bean ({@code @StepScope}) into the
 * StepExecution's ExecutionContext — which the controller reads back after
 * {@code jobLauncher.run(...)}.
 *
 * <p>IMPORTANT: always go through {@link PatientTreeItemWriter}'s getters
 * (never through its fields directly) — see that class's Javadoc: the
 * writer injected here is a {@code @StepScope} proxy, and direct field
 * access bypasses the proxy (guaranteed NullPointerException).
 */
public class ImportResultStepListener implements StepExecutionListener {

    private final PatientTreeItemWriter writer;

    public ImportResultStepListener(PatientTreeItemWriter writer) {
        this.writer = writer;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        ExecutionContext ctx = stepExecution.getExecutionContext();
        ctx.putInt("patientsImported", writer.getPatientsImported());
        ctx.putInt("patientsFailed", writer.getPatientsFailed());
        ctx.putInt("consultationsImported", writer.getConsultationsImported());
        ctx.putInt("treatmentsImported", writer.getTreatmentsImported());
        ctx.put("errors", new ArrayList<>(writer.getErrors()));
        return stepExecution.getExitStatus();
    }
}