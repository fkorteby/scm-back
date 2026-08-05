package com.simple_cabinet_medical.Backend.service.MdbImport;

import com.simple_cabinet_medical.Backend.service.MdbImport.ImportRowPersister.PatientImportOutcome;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Writer chunk-oriented : reçoit un lot de patients (taille = chunk-size du
 * step) et sauvegarde CHACUN indépendamment via
 * ImportRowPersister.savePatientTree(...) (transaction REQUIRES_NEW par
 * patient, déjà en place).
 *
 * IMPORTANT : ce bean est @StepScope, donc Spring injecte un PROXY CGLIB
 * partout où on le référence (y compris dans le listener). Un proxy CGLIB
 * n'intercepte que les APPELS DE MÉTHODE, jamais l'accès direct à un champ
 * public — d'où l'obligation d'exposer les compteurs via des getters, et de
 * ne JAMAIS accéder aux champs directement depuis l'extérieur de la classe.
 */
public class PatientTreeItemWriter implements ItemWriter<PatientImportUnit> {

    private final ImportRowPersister rowPersister;

    private final AtomicInteger patientsImported      = new AtomicInteger(0);
    private final AtomicInteger patientsFailed        = new AtomicInteger(0);
    private final AtomicInteger consultationsImported = new AtomicInteger(0);
    private final AtomicInteger traitementsImported   = new AtomicInteger(0);
    private final List<String>  errors                = new ArrayList<>();

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
                traitementsImported.addAndGet(outcome.traitementsSaved());

            } catch (Exception e) {
                patientsFailed.incrementAndGet();
                synchronized (errors) {
                    errors.add("Patient num_mal=" + unit.numMal()
                            + " ignoré (avec toutes ses consultations/traitements) : "
                            + ImportErrorFormatter.describe(e));
                }
            }
        }
    }

    // ─── Getters (obligatoires : voir Javadoc de classe sur le proxy @StepScope) ─

    public int getPatientsImported()      { return patientsImported.get(); }
    public int getPatientsFailed()        { return patientsFailed.get(); }
    public int getConsultationsImported() { return consultationsImported.get(); }
    public int getTraitementsImported()   { return traitementsImported.get(); }

    public List<String> getErrors() {
        synchronized (errors) {
            return new ArrayList<>(errors);
        }
    }
}