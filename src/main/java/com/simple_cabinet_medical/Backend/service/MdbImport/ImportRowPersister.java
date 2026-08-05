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
 * Persiste le "bloc" complet d'un patient : le patient lui-même, toutes ses
 * consultations, et tous les traitements de ces consultations — en UNE seule
 * transaction (REQUIRES_NEW).
 *
 * Pourquoi une transaction par patient (et pas par ligne, ni une seule transaction
 * globale) :
 *  - Une seule transaction globale : la moindre ligne en erreur, sur des millions
 *    de lignes, annule tout l'import. Inacceptable.
 *  - Une transaction par ligne (patient, puis chaque consultation, puis chaque
 *    traitement séparément) : correct mais beaucoup trop de commits sur une grosse
 *    base (des millions de transactions), donc très lent.
 *  - Une transaction par PATIENT (ce fichier) : bon compromis. Si un patient a un
 *    souci réel de contrainte BDD au moment du flush, on perd CE patient et son
 *    arborescence (consultations + traitements), mais tous les autres patients
 *    continuent d'être importés normalement. Comme tu retires les contraintes sur
 *    la table patient, ce cas doit devenir rare : le patient passe presque toujours,
 *    et l'essentiel du filtrage (patient introuvable, consultation introuvable)
 *    est déjà fait AVANT d'arriver ici (voir MdbImportService / MdbImportServiceV1).
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
     * Un médicament est une donnée partagée/globale (clientCreatorId=1L, cache
     * commun) — indépendante de la réussite ou de l'échec d'un patient donné.
     * On la sauvegarde donc dans SA PROPRE petite transaction, séparée du bloc
     * patient : si le patient échoue plus tard, le médicament déjà créé reste
     * (c'est sans conséquence, juste une entrée de catalogue en plus).
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Medicament saveMedicament(Medicament m) {
        return medicamentRepository.saveAndFlush(m);
    }

    /**
     * Sauvegarde patient + consultations + traitements comme un seul bloc.
     * Si une exception survient n'importe où là-dedans (contrainte BDD, etc.),
     * TOUT le bloc est annulé (rollback automatique de la transaction REQUIRES_NEW),
     * et l'exception remonte à l'appelant pour être journalisée proprement.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PatientImportOutcome savePatientTree(Patient patient, List<ConsultationUnit> consultationUnits) {
        Patient savedPatient = patientRepository.saveAndFlush(patient);

        int consultationsSaved = 0;
        List<Traitement> allTraitements = new ArrayList<>();

        for (ConsultationUnit unit : consultationUnits) {
            unit.consultation().setPatient(savedPatient);
            Consultation savedConsultation = consultationRepository.saveAndFlush(unit.consultation());
            consultationsSaved++;

            for (Traitement t : unit.traitements()) {
                t.setConsultation(savedConsultation);
                allTraitements.add(t);
            }
        }

        if (!allTraitements.isEmpty()) {
            traitementRepository.saveAll(allTraitements);
        }

        return new PatientImportOutcome(consultationsSaved, allTraitements.size());
    }

    // ─── Structures de transport (préparées AVANT la transaction, en lecture seule) ─

    /** Une consultation prête à être persistée, avec ses traitements déjà résolus. */
    public record ConsultationUnit(Consultation consultation, List<Traitement> traitements) {
    }

    /** Résultat chiffré de la sauvegarde d'un bloc patient. */
    public record PatientImportOutcome(int consultationsSaved, int traitementsSaved) {
    }
}