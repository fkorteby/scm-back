package com.simple_cabinet_medical.Backend.Permission;

import com.simple_cabinet_medical.Backend.repository.ConsultationRepository;
import com.simple_cabinet_medical.Backend.repository.DocumentRepository;
import com.simple_cabinet_medical.Backend.repository.PatientRepository;
import com.simple_cabinet_medical.Backend.repository.TraitementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


@Component
public class EntityFetcher {
    private static final Logger log = LoggerFactory.getLogger(EntityFetcher.class);

    private final ConsultationRepository consultationRepository;
    private final DocumentRepository documentRepository;
    private final TraitementRepository traitementRepository;

    public EntityFetcher(@Lazy ConsultationRepository consultationRepository,
                         @Lazy PatientRepository patientRepository,
                         @Lazy DocumentRepository documentRepository,
                         @Lazy TraitementRepository traitementRepository) {
        this.consultationRepository = consultationRepository;
        this.documentRepository = documentRepository;
        this.traitementRepository = traitementRepository;
    }

    public Object getEntity(String type, Long id) {

        switch (type) {
            case "Consultation":
                return consultationRepository.findById(id).orElse(null);
            case "Document":
                return documentRepository.findById(id).orElse(null);
            case "Traitement":
                return traitementRepository.findById(id).orElse(null);

            default:
                return null;
        }
    }
}

