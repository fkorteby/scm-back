package com.simple_cabinet_medical.Backend.importation.processor;

import com.simple_cabinet_medical.Backend.importation.domain.ImportedConsultation;
import com.simple_cabinet_medical.Backend.importation.domain.OldConsultation;
import com.simple_cabinet_medical.Backend.importation.domain.OldPrescription;
import com.simple_cabinet_medical.Backend.importation.lookup.PrescLookup;
import com.simple_cabinet_medical.Backend.importation.mapping.PatientMapping;
import com.simple_cabinet_medical.Backend.model.Consultation;
import com.simple_cabinet_medical.Backend.model.EStatusConsultation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;
import java.util.List;

@Slf4j
public class ConsultationProcessor
        implements ItemProcessor<OldConsultation, ImportedConsultation> {

    private final Long clientId;
    private final PatientMapping patientMapping;
    private final PrescLookup prescLookup;

    public ConsultationProcessor(
            Long clientId,
            PatientMapping patientMapping,
            PrescLookup prescLookup
    ) {
        this.clientId = clientId;
        this.patientMapping = patientMapping;
        this.prescLookup = prescLookup;
    }

    @Override
    public ImportedConsultation process(
            OldConsultation oldConsultation
    ) throws Exception {

        Long newPatientId =
                patientMapping.get(oldConsultation.numMal());

        log.info(
                "CONSULTATION MAPPING: numMal={} -> patientId={}",
                oldConsultation.numMal(),
                newPatientId
        );

        if (newPatientId == null) {

            log.warn(
                    "Patient not found for consultation: numCons={}, numMal={}",
                    oldConsultation.numCons(),
                    oldConsultation.numMal()
            );

            return null;
        }

        Consultation consultation = new Consultation();

        consultation.setDateConsultation(
                oldConsultation.dateCons() != null
                        ? oldConsultation.dateCons().toLocalDate()
                        : LocalDate.now()
        );

        consultation.setMotifConsultation(
                clean(oldConsultation.motif())
        );

        consultation.setDiagnosticMedical(
                clean(oldConsultation.diag())
        );

        consultation.setResultatExamenClinique(
                clean(oldConsultation.resultatExamen())
        );

        consultation.setResultatExamenParacliniques(
                clean(oldConsultation.resultatPara())
        );

        consultation.setCatEvolution(
                clean(oldConsultation.conduiteCons())
        );

        consultation.setStatusConsultation(
                EStatusConsultation.TERMINEE
        );

        consultation.setClientCreatorId(clientId);

        /*
         * ============================================================
         * TRAITEMENT TEXTE
         * ============================================================
         */

        String traitCons =
                clean(oldConsultation.traitCons());

        String trait2Cons =
                clean(oldConsultation.trait2Cons());

        if (traitCons != null) {

            consultation.setTraitement(traitCons);

        } else if (trait2Cons != null) {

            consultation.setTraitement(trait2Cons);
        }

        /*
         * ============================================================
         * PRESCRIPTIONS STRUCTURÉES
         * ============================================================
         *
         * IMPORTANT :
         * On cherche TOUJOURS les prescriptions dans presc,
         * même si trait_cons ou trait2_cons existe.
         */

        List<OldPrescription> prescriptions =
                prescLookup.findByConsultation(
                        oldConsultation.numCons()
                );

        log.info(
                "PRESCRIPTIONS: numCons={} -> {} prescription(s)",
                oldConsultation.numCons(),
                prescriptions.size()
        );

        return new ImportedConsultation(
                oldConsultation.numCons(),
                oldConsultation.numMal(),
                newPatientId,
                consultation,
                prescriptions
        );
    }

    private String clean(String value) {

        if (value == null) {
            return null;
        }

        String cleaned = value.trim();

        return cleaned.isEmpty()
                ? null
                : cleaned;
    }
}