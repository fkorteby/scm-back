package com.simple_cabinet_medical.Backend.service.MdbImport;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;

import java.util.ArrayList;

/**
 * A la fin du step, recopie les compteurs et messages d'erreur accumulés par
 * le writer (bean @StepScope) dans l'ExecutionContext du StepExecution — que
 * le contrôleur relira après jobLauncher.run(...).
 *
 * IMPORTANT : on passe TOUJOURS par les getters de PatientTreeItemWriter
 * (jamais par ses champs directement) — voir la Javadoc de cette classe :
 * le writer injecté ici est un proxy @StepScope, et un accès direct à un
 * champ contourne le proxy (NullPointerException garantie).
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
        ctx.putInt("traitementsImported", writer.getTraitementsImported());
        ctx.put("errors", new ArrayList<>(writer.getErrors()));
        return stepExecution.getExitStatus();
    }
}