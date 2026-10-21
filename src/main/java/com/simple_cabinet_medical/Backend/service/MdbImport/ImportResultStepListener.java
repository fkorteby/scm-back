package com.simple_cabinet_medical.Backend.service.MdbImport;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;

import java.util.ArrayList;

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