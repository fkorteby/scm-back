package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.model.Consultation;
import com.simple_cabinet_medical.Backend.model.EAccessLevel;
import com.simple_cabinet_medical.Backend.repository.ConsultationRepository;
import com.simple_cabinet_medical.Backend.utils.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final AccessControlService accessControlService;

    public ConsultationService(ConsultationRepository consultationRepository, AccessControlService accessControlService) {
        this.consultationRepository = consultationRepository;
        this.accessControlService = accessControlService;
    }

    public ResponseEntity<?> addConsultation (Long idUtilisateur) {
        if (accessControlService.hasAccess(new Consultation(), idUtilisateur, EAccessLevel.WRITE)) {
            return ResponseHandler.generateResponse("", HttpStatus.OK, null);
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }

    public ResponseEntity<?> deleteConsultation (Long idUtilisateur) {
        if (accessControlService.hasAccess(new Consultation(), idUtilisateur, EAccessLevel.DELETE)) {
            return ResponseHandler.generateResponse("", HttpStatus.OK, null);
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }

    public ResponseEntity<?> updateConsultation (Long idUtilisateur) {
        if (accessControlService.hasAccess(new Consultation(), idUtilisateur, EAccessLevel.UPDATE)) {
            return ResponseHandler.generateResponse("", HttpStatus.OK, null);
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }

    public ResponseEntity<?> getAllConsultation (Long idUtilisateur) {
        if (accessControlService.hasAccess(new Consultation(), idUtilisateur, EAccessLevel.READ)) {
            return ResponseHandler.generateResponse("", HttpStatus.OK, null);
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }

    public ResponseEntity<?> findConsultation (Long idUtilisateur) {
        if (accessControlService.hasAccess(new Consultation(), idUtilisateur, EAccessLevel.READ)) {
            return ResponseHandler.generateResponse("", HttpStatus.OK, null);
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }


}
