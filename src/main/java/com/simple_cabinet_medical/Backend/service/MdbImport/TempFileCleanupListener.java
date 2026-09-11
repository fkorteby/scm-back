package com.simple_cabinet_medical.Backend.service.MdbImport;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Supprime le fichier .mdb temporaire une fois le Job VRAIMENT terminé
 * (succès ou échec). Indispensable maintenant que le Job tourne en
 * asynchrone : on ne peut plus supprimer le fichier juste après avoir
 * lancé le Job dans le contrôleur (comme avant), puisque le Job est
 * encore en train de le lire à ce moment-là.
 */
public class TempFileCleanupListener implements JobExecutionListener {

    @Override
    public void afterJob(JobExecution jobExecution) {
        String filePath = jobExecution.getJobParameters().getString("filePath");
        if (filePath == null) return;

        try {
            Files.deleteIfExists(Path.of(filePath));
        } catch (IOException ignored) {
            // Non bloquant : au pire un fichier temporaire orphelin dans /tmp.
        }
    }
}