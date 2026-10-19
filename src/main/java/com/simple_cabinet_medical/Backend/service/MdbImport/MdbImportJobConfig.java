//package com.simple_cabinet_medical.Backend.service.MdbImport;
//
//import com.simple_cabinet_medical.Backend.repository.ClientRepository;
//import com.simple_cabinet_medical.Backend.repository.MedicamentRepository;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.StepScope;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.task.SimpleAsyncTaskExecutor;
//import org.springframework.core.task.TaskExecutor;
//import org.springframework.transaction.PlatformTransactionManager;
//
///**
// * Single Job/Step for the MDB import, applicable to every file regardless
// * of its version — MdbPatientItemReader detects optional columns/tables
// * itself at open() time.
// *
// * <p>CHUNK_SIZE = number of patients grouped together before a Spring Batch
// * commit/checkpoint. It does not affect per-patient atomicity (handled by
// * ImportRowPersister via REQUIRES_NEW inside the writer) — it only limits
// * how often Spring Batch metadata tables are written.
// *
// * <p>The step runs on a bounded TaskExecutor so multiple chunks can be
// * processed concurrently: each patient is already persisted in its own
// * independent REQUIRES_NEW transaction, so this directly targets the real
// * bottleneck for remote databases (Cloud SQL): network round-trip latency
// * per statement. Make sure spring.datasource.hikari.maximum-pool-size is at
// * least TASK_EXECUTOR_CONCURRENCY_LIMIT.
// */
//@Configuration
//public class MdbImportJobConfig {
//
//    private static final int CHUNK_SIZE = 100;
//  //  private static final int TASK_EXECUTOR_CONCURRENCY_LIMIT = 8;
//
//    private final JobRepository jobRepository;
//    private final PlatformTransactionManager transactionManager;
//    private final ClientRepository clientRepository;
//    private final MedicamentRepository medicamentRepository;
//    private final ImportRowPersister rowPersister;
//
//    public MdbImportJobConfig(JobRepository jobRepository,
//                              PlatformTransactionManager transactionManager,
//                              ClientRepository clientRepository,
//                              MedicamentRepository medicamentRepository,
//                              ImportRowPersister rowPersister) {
//        this.jobRepository = jobRepository;
//        this.transactionManager = transactionManager;
//        this.clientRepository = clientRepository;
//        this.medicamentRepository = medicamentRepository;
//        this.rowPersister = rowPersister;
//    }
//
//    @Bean
//    public TaskExecutor mdbImportTaskExecutor() {
//        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("mdb-import-");
//        return executor;
//    }
//
//    @Bean
//    @StepScope
//    public MdbPatientItemReader mdbPatientItemReader(
//            @Value("#{jobParameters['filePath']}") String filePath,
//            @Value("#{jobParameters['password']}") String password,
//            @Value("#{jobParameters['clientId']}") Long clientId) {
//        return new MdbPatientItemReader(filePath, password, clientId,
//                clientRepository, medicamentRepository, rowPersister);
//    }
//
//    @Bean
//    @StepScope
//    public PatientTreeItemWriter mdbPatientItemWriter() {
//        return new PatientTreeItemWriter(rowPersister);
//    }
//
//    @Bean
//    public Step mdbImportStep(MdbPatientItemReader mdbPatientItemReader,
//                              PatientTreeItemWriter mdbPatientItemWriter,
//                              TaskExecutor mdbImportTaskExecutor) {
//        return new StepBuilder("mdbImportStep", jobRepository)
//                .<PatientImportUnit, PatientImportUnit>chunk(CHUNK_SIZE, transactionManager)
//                .reader(mdbPatientItemReader)
//                .writer(mdbPatientItemWriter)
//                .taskExecutor(mdbImportTaskExecutor)
//                .listener(new ImportResultStepListener(mdbPatientItemWriter))
//                .build();
//    }
//
//    @Bean
//    public Job mdbImportJob(Step mdbImportStep) {
//        return new JobBuilder("mdbImportJob", jobRepository)
//                .start(mdbImportStep)
//                .listener(new TempFileCleanupListener())
//                .build();
//    }
//}