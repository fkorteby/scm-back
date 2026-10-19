package com.simple_cabinet_medical.Backend.importation.util;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class MdbConnectionListener
        implements StepExecutionListener {

    private final MdbConnection mdbConnection;

    public MdbConnectionListener(
            MdbConnection mdbConnection
    ) {
        this.mdbConnection = mdbConnection;
    }

    @Override
    public void beforeStep(
            StepExecution stepExecution
    ) {
        // Connexion déjà ouverte.
    }

    @Override
    public ExitStatus afterStep(
            StepExecution stepExecution
    ) {

        mdbConnection.close();

        return stepExecution.getExitStatus();
    }
}