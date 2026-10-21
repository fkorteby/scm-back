package com.simple_cabinet_medical.Backend.service.MdbImport;

import com.simple_cabinet_medical.Backend.service.MdbImport.ImportRowPersister.PatientImportOutcome;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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