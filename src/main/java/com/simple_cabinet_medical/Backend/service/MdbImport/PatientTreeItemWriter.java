package com.simple_cabinet_medical.Backend.service.MdbImport;

import com.simple_cabinet_medical.Backend.service.MdbImport.ImportRowPersister.PatientImportOutcome;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Chunk-oriented writer: receives a batch of patients (size = the step's
 * chunk size) and saves EACH one independently via
 * {@link ImportRowPersister#savePatientTree}, which already runs in its own
 * REQUIRES_NEW transaction per patient.
 *
 * <p>IMPORTANT: this bean is {@code @StepScope}, so Spring injects a CGLIB
 * PROXY everywhere it is referenced (including in the listener). A CGLIB
 * proxy only intercepts METHOD CALLS, never direct field access — hence the
 * counters are exposed only through getters, never accessed as raw fields
 * from outside this class.
 *
 * <p>Thread-safety: with the step's {@code TaskExecutor} enabled (see
 * {@link MdbImportJobConfig}), {@code write(...)} can be called concurrently
 * for different chunks. All counters use {@link AtomicInteger} and the error
 * list is synchronized accordingly.
 */
public class PatientTreeItemWriter implements ItemWriter<PatientImportUnit> {

    private final ImportRowPersister rowPersister;

    private final AtomicInteger patientsImported      = new AtomicInteger(0);
    private final AtomicInteger patientsFailed        = new AtomicInteger(0);
    private final AtomicInteger consultationsImported = new AtomicInteger(0);
    private final AtomicInteger treatmentsImported     = new AtomicInteger(0);
    private final List<String>  errors                 = new ArrayList<>();

    public PatientTreeItemWriter(ImportRowPersister rowPersister) {
        this.rowPersister = rowPersister;
    }

    @Override
    public void write(Chunk<? extends PatientImportUnit> chunk) {
        for (PatientImportUnit unit : chunk) {
            try {
                PatientImportOutcome outcome =
                        rowPersister.savePatientTree(unit.patient(), unit.consultationUnits());

                patientsImported.incrementAndGet();
                consultationsImported.addAndGet(outcome.consultationsSaved());
                treatmentsImported.addAndGet(outcome.treatmentsSaved());


            } catch (Exception e) {
                patientsFailed.incrementAndGet();
                synchronized (errors) {
                    errors.add("Patient num_mal=" + unit.numMal()
                            + " skipped (along with all its consultations/treatments): "
                            + ImportErrorFormatter.describe(e));
                }
            }
        }
    }

    // ─── Getters (mandatory: see class Javadoc about the @StepScope proxy) ──

    public int getPatientsImported()      { return patientsImported.get(); }
    public int getPatientsFailed()        { return patientsFailed.get(); }
    public int getConsultationsImported() { return consultationsImported.get(); }
    public int getTreatmentsImported()    { return treatmentsImported.get(); }

    public List<String> getErrors() {
        synchronized (errors) {
            return new ArrayList<>(errors);
        }
    }
}