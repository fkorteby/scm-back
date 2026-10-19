package com.simple_cabinet_medical.Backend.config;

import com.simple_cabinet_medical.Backend.importation.domain.ImportedConsultation;
import com.simple_cabinet_medical.Backend.importation.domain.ImportedPatient;
import com.simple_cabinet_medical.Backend.importation.domain.OldConsultation;
import com.simple_cabinet_medical.Backend.importation.domain.OldPatient;
import com.simple_cabinet_medical.Backend.importation.lookup.PrescLookup;
import com.simple_cabinet_medical.Backend.importation.mapping.PatientMapping;
import com.simple_cabinet_medical.Backend.importation.processor.ConsultationProcessor;
import com.simple_cabinet_medical.Backend.importation.processor.PatientProcessor;
import com.simple_cabinet_medical.Backend.importation.reader.ConsultationReader;
import com.simple_cabinet_medical.Backend.importation.reader.PatientReader;
import com.simple_cabinet_medical.Backend.importation.util.MdbConnection;
import com.simple_cabinet_medical.Backend.importation.util.MdbConnectionListener;
import com.simple_cabinet_medical.Backend.importation.writer.ConsultationWriter;
import com.simple_cabinet_medical.Backend.importation.writer.PatientWriter;
import com.simple_cabinet_medical.Backend.model.Patient;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public BatchConfig(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager
    ) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public TaskExecutorJobLauncher asyncJobLauncher() throws Exception {
        TaskExecutorJobLauncher launcher = new TaskExecutorJobLauncher();
        launcher.setJobRepository(jobRepository);
        launcher.setTaskExecutor(new SimpleAsyncTaskExecutor("mdb-import-"));
        launcher.afterPropertiesSet();
        return launcher;
    }

    @Bean
    public Job importDataJob(Step importPatientsStep, Step importConsultationsStep) {

        return new JobBuilder("importDataJob", jobRepository)
                .start(importPatientsStep)
                .next(importConsultationsStep)
                .build();
    }

    @Bean
    public Step importPatientsStep(
            PatientReader patientReader,
            PatientProcessor patientProcessor,
            PatientWriter patientWriter) {

        return new StepBuilder("importPatientsStep", jobRepository)
                .<OldPatient, ImportedPatient>chunk(
                        500,
                        transactionManager
                )
                .reader(patientReader)
                .processor(patientProcessor)
                .writer(patientWriter)
                .build();
    }

    @Bean
    @StepScope
    public PatientReader patientReader(@Value("#{jobParameters['filePath']}") String filePath) {
        return new PatientReader(filePath);
    }

    @Bean
    @StepScope
    public PatientProcessor patientProcessor(@Value("#{jobParameters['clientId']}") Long clientId) {
        return new PatientProcessor(clientId);
    }

    @Bean
    @StepScope
    public PatientWriter patientWriter(
            DataSource dataSource,
            @Value("#{jobParameters['clientId']}") Long clientId,
            PatientMapping patientMapping
    ) {
        return new PatientWriter(
                dataSource,
                clientId,
                patientMapping
        );
    }

    @Bean
    @StepScope
    public MdbConnection mdbConnection(
            @Value("#{jobParameters['filePath']}")
            String filePath
    ) throws Exception {

        MdbConnection mdbConnection =
                new MdbConnection(filePath);

        mdbConnection.open();

        return mdbConnection;
    }

    @Bean
    @StepScope
    public ConsultationReader consultationReader(
            MdbConnection mdbConnection
    ) {
        return new ConsultationReader(mdbConnection);
    }

    @Bean
    @StepScope
    public PrescLookup prescLookup(
            MdbConnection mdbConnection
    ) throws Exception {

        PrescLookup lookup =
                new PrescLookup(mdbConnection);

        lookup.initialize();

        return lookup;
    }

    @Bean
    public Step importConsultationsStep(
            ConsultationReader consultationReader,
            ConsultationProcessor consultationProcessor,
            ConsultationWriter consultationWriter,
            MdbConnection mdbConnection
    ) {

        return new StepBuilder(
                "importConsultationsStep",
                jobRepository
        )
                .<OldConsultation, ImportedConsultation>chunk(
                        500,
                        transactionManager
                )
                .reader(consultationReader)
                .processor(consultationProcessor)
                .writer(consultationWriter)

                // Permet à ConsultationWriter de recevoir beforeStep()
                .listener(consultationWriter)

                // Ferme la connexion MDB à la fin
                .listener(
                        new MdbConnectionListener(mdbConnection)
                )

                .build();
    }

    @Bean
    @StepScope
    public ConsultationProcessor consultationProcessor(
            @Value("#{jobParameters['clientId']}")
            Long clientId,
            PatientMapping patientMapping,
            PrescLookup prescLookup
    ) {
        return new ConsultationProcessor(
                clientId,
                patientMapping,
                prescLookup
        );
    }

    @Bean
    @StepScope
    public ConsultationWriter consultationWriter(
            DataSource dataSource,
            @Value("#{jobParameters['clientId']}") Long clientId
    ) {
        return new ConsultationWriter(
                dataSource,
                clientId
        );
    }
}