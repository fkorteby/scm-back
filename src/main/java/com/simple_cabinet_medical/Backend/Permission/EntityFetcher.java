package com.simple_cabinet_medical.Backend.Permission;

import com.simple_cabinet_medical.Backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


@Component
public class EntityFetcher {
    private static final Logger log = LoggerFactory.getLogger(EntityFetcher.class);

    private final ConsultationRepository consultationRepository;
    private final PatientRepository patientRepository;
    private final OrdonnanceRepository ordonnanceRepository;
    private final DocumentRepository documentRepository;
    private final TraitementRepository traitementRepository;

    public EntityFetcher(@Lazy ConsultationRepository consultationRepository,
                         @Lazy PatientRepository patientRepository,
                         @Lazy OrdonnanceRepository ordonnanceRepository,
                         @Lazy DocumentRepository documentRepository,
                         @Lazy TraitementRepository traitementRepository) {
        this.consultationRepository = consultationRepository;
        this.patientRepository = patientRepository;
        this.ordonnanceRepository = ordonnanceRepository;
        this.documentRepository = documentRepository;
        this.traitementRepository = traitementRepository;
    }

    public Object getEntity(String type, Long id) {

        switch (type) {
            case "Consultation":
                return consultationRepository.findById(id).orElse(null);
            case "Patient":
                return patientRepository.findById(id).orElse(null);
            case "Ordonnance":
                return ordonnanceRepository.findById(id).orElse(null);
            case "Document":
                return documentRepository.findById(id).orElse(null);
            case "Traitement":
                return traitementRepository.findById(id).orElse(null);

            default:
                return null;
        }
    }
}

