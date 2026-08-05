package com.simple_cabinet_medical.Backend.service.MdbImport;

import com.simple_cabinet_medical.Backend.repository.ClientRepository;
import com.simple_cabinet_medical.Backend.repository.MedicamentRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.batch.core.configuration.annotation.StepScope;

/**
 * Deux Jobs distincts, un par version de fichier MDB (plus simple et plus
 * lisible qu'un seul Job avec un reader qui commute dynamiquement selon un
 * paramètre). Le contrôleur détecte la version (MdbVersionDetector) et lance
 * le bon Job.
 *
 * CHUNK_SIZE = nombre de patients regroupés avant un commit/checkpoint côté
 * Spring Batch. Ça n'a pas d'impact sur l'atomicité par patient (gérée par
 * ImportRowPersister en REQUIRES_NEW dans le writer) — ça sert uniquement à
 * limiter la fréquence des écritures dans les tables de métadonnées Spring
 * Batch (BATCH_STEP_EXECUTION_CONTEXT, etc.). 50 est un bon point de départ ;
 * pas besoin de le régler finement puisque le vrai travail (I/O MDB, écriture
 * JPA) n'est pas affecté par cette taille.
 */
@Configuration
public class MdbImportJobConfig {

    private static final int CHUNK_SIZE = 50;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ClientRepository clientRepository;
    private final MedicamentRepository medicamentRepository;
    private final ImportRowPersister rowPersister;

    public MdbImportJobConfig(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              ClientRepository clientRepository,
                              MedicamentRepository medicamentRepository,
                              ImportRowPersister rowPersister) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.clientRepository = clientRepository;
        this.medicamentRepository = medicamentRepository;
        this.rowPersister = rowPersister;
    }

    // ════════════════════════════════════════════════════════════════════
    //  Version 1.7 (avec presc)
    // ════════════════════════════════════════════════════════════════════

    @Bean
    @StepScope
    public MdbPatientItemReader mdbPatientItemReaderV17(
            @Value("#{jobParameters['filePath']}") String filePath,
            @Value("#{jobParameters['password']}") String password,
            @Value("#{jobParameters['clientId']}") Long clientId) {
        return new MdbPatientItemReader(filePath, password, clientId,
                clientRepository, medicamentRepository, rowPersister);
    }

    @Bean
    @StepScope
    public PatientTreeItemWriter mdbPatientItemWriterV17() {
        return new PatientTreeItemWriter(rowPersister);
    }

    @Bean
    public Step mdbImportStepV17(MdbPatientItemReader mdbPatientItemReaderV17,
                                 PatientTreeItemWriter mdbPatientItemWriterV17) {
        return new StepBuilder("mdbImportStepV17", jobRepository)
                .<PatientImportUnit, PatientImportUnit>chunk(CHUNK_SIZE, transactionManager)
                .reader(mdbPatientItemReaderV17)
                .writer(mdbPatientItemWriterV17)
                .listener(new ImportResultStepListener(mdbPatientItemWriterV17))
                .build();
    }

    @Bean
    public Job mdbImportJobV17(Step mdbImportStepV17) {
        return new JobBuilder("mdbImportJobV17", jobRepository)
                .start(mdbImportStepV17)
                .build();
    }

    // ════════════════════════════════════════════════════════════════════
    //  Version 1.0 (sans presc)
    // ════════════════════════════════════════════════════════════════════

    @Bean
    @StepScope
    public MdbPatientItemReaderV1 mdbPatientItemReaderV10(
            @Value("#{jobParameters['filePath']}") String filePath,
            @Value("#{jobParameters['password']}") String password,
            @Value("#{jobParameters['clientId']}") Long clientId) {
        return new MdbPatientItemReaderV1(filePath, password, clientId,
                clientRepository, medicamentRepository, rowPersister);
    }

    @Bean
    @StepScope
    public PatientTreeItemWriter mdbPatientItemWriterV10() {
        return new PatientTreeItemWriter(rowPersister);
    }

    @Bean
    public Step mdbImportStepV10(MdbPatientItemReaderV1 mdbPatientItemReaderV10,
                                 PatientTreeItemWriter mdbPatientItemWriterV10) {
        return new StepBuilder("mdbImportStepV10", jobRepository)
                .<PatientImportUnit, PatientImportUnit>chunk(CHUNK_SIZE, transactionManager)
                .reader(mdbPatientItemReaderV10)
                .writer(mdbPatientItemWriterV10)
                .listener(new ImportResultStepListener(mdbPatientItemWriterV10))
                .build();
    }

    @Bean
    public Job mdbImportJobV10(Step mdbImportStepV10) {
        return new JobBuilder("mdbImportJobV10", jobRepository)
                .start(mdbImportStepV10)
                .build();
    }
}