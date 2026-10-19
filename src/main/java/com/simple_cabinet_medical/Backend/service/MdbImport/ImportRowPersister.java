package com.simple_cabinet_medical.Backend.service.MdbImport;

import com.simple_cabinet_medical.Backend.model.Consultation;
import com.simple_cabinet_medical.Backend.model.Medicament;
import com.simple_cabinet_medical.Backend.model.Patient;
import com.simple_cabinet_medical.Backend.model.Traitement;
import com.simple_cabinet_medical.Backend.repository.ConsultationRepository;
import com.simple_cabinet_medical.Backend.repository.MedicamentRepository;
import com.simple_cabinet_medical.Backend.repository.PatientRepository;
import com.simple_cabinet_medical.Backend.repository.TraitementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Persists a patient's whole "tree" — the patient itself, all of its
 * consultations, and all treatments of those consultations — in ONE single
 * transaction (REQUIRES_NEW).
 *
 * <p>Why one transaction per patient (and not per row, nor one global
 * transaction for the whole import):
 * <ul>
 *   <li>One single global transaction: a single failing row, out of
 *       potentially millions, rolls back the entire import. Unacceptable.</li>
 *   <li>One transaction per row (patient, then each consultation, then each
 *       treatment separately): correct but far too many commits on a large
 *       database (millions of transactions) — very slow.</li>
 *   <li>One transaction per PATIENT (this class): good compromise. If a
 *       patient hits a real DB constraint issue at flush time, only THAT
 *       patient and its subtree (consultations + treatments) is lost, while
 *       every other patient keeps importing normally.</li>
 * </ul>
 *
 * <p><b>Performance note (see SPRING_BATCH_GUIDE.md §4.1):</b> consultations
 * are now saved with a single {@code saveAll(...) + flush()} for the whole
 * patient, instead of one {@code saveAndFlush} per consultation. Flushing
 * once lets Hibernate group the INSERT statements into a real JDBC batch
 * (governed by {@code hibernate.jdbc.batch_size}) rather than sending one
 * round trip per consultation. This only works if the entity's
 * {@code @GeneratedValue} strategy is NOT {@code IDENTITY} — check
 * {@code Patient}/{@code Consultation}/{@code Traitement} use
 * {@code GenerationType.SEQUENCE} instead, otherwise Hibernate silently
 * disables batching regardless of this change.
 */
@Service
public class ImportRowPersister {

    private final PatientRepository patientRepository;
    private final ConsultationRepository consultationRepository;
    private final TraitementRepository traitementRepository;
    private final MedicamentRepository medicamentRepository;

    public ImportRowPersister(PatientRepository patientRepository,
                              ConsultationRepository consultationRepository,
                              TraitementRepository traitementRepository,
                              MedicamentRepository medicamentRepository) {
        this.patientRepository = patientRepository;
        this.consultationRepository = consultationRepository;
        this.traitementRepository = traitementRepository;
        this.medicamentRepository = medicamentRepository;
    }

    /**
     * A treatment/drug is shared/global reference data (clientCreatorId=1L,
     * shared cache) — independent from whether a given patient ultimately
     * succeeds or fails. It is therefore saved in ITS OWN small transaction,
     * separate from the patient block: if the patient fails later, the drug
     * already created is kept (harmless — just one extra catalog entry).
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Medicament saveMedicament(Medicament m) {
        return medicamentRepository.saveAndFlush(m);
    }

    /**
     * Saves patient + consultations + treatments as a single block. If an
     * exception occurs anywhere in here (DB constraint, etc.), the WHOLE
     * block is rolled back (automatic rollback of the REQUIRES_NEW
     * transaction), and the exception propagates to the caller to be logged.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PatientImportOutcome savePatientTree(Patient patient, List<ConsultationUnit> consultationUnits) {
        // Sequence-generated IDs are assigned at persist() time, so
        // savedPatient.getId() is already available here without a flush —
        // no need to force a round trip just to link consultations to it.
        Patient savedPatient = patientRepository.save(patient);

        List<Consultation> consultationsToPersist = new ArrayList<>(consultationUnits.size());
        for (ConsultationUnit unit : consultationUnits) {
            unit.consultation().setPatient(savedPatient);
            consultationsToPersist.add(unit.consultation());
        }

        // Single batched flush for every consultation of this patient
        // (was previously one flush per consultation in the loop, which
        // defeated Hibernate's JDBC batching entirely).
        List<Consultation> savedConsultations = consultationRepository.saveAll(consultationsToPersist);
        consultationRepository.flush();

        List<Traitement> allTraitements = new ArrayList<>();
        for (int i = 0; i < consultationUnits.size(); i++) {
            Consultation savedConsultation = savedConsultations.get(i);
            for (Traitement t : consultationUnits.get(i).traitements()) {
                t.setConsultation(savedConsultation);
                allTraitements.add(t);
            }
        }

        if (!allTraitements.isEmpty()) {
            traitementRepository.saveAll(allTraitements);
            // No explicit flush needed: nothing else in this transaction
            // needs these rows to already be visible, so let the
            // REQUIRES_NEW transaction commit flush them.
        }

        return new PatientImportOutcome(savedConsultations.size(), allTraitements.size());
    }

    // ─── Transport structures (built BEFORE the transaction, read-only) ────

    /** A consultation ready to be persisted, with its treatments already resolved. */
    public record ConsultationUnit(Consultation consultation, List<Traitement> traitements) {
    }

    /** Numeric result of persisting one patient block. */
    public record PatientImportOutcome(int consultationsSaved, int treatmentsSaved) {
    }
}