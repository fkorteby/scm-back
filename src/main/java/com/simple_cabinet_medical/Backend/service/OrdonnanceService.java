package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.model.Consultation;
import com.simple_cabinet_medical.Backend.model.EAccessLevel;
import com.simple_cabinet_medical.Backend.model.Ordonnance;
import com.simple_cabinet_medical.Backend.repository.OrdonnanceRepository;
import com.simple_cabinet_medical.Backend.utils.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class OrdonnanceService {

    private final OrdonnanceRepository ordonnanceRepository;
    private final AccessControlService accessControlService;

    public OrdonnanceService(OrdonnanceRepository ordonnanceRepository, AccessControlService accessControlService) {
        this.ordonnanceRepository = ordonnanceRepository;
        this.accessControlService = accessControlService;
    }

    public ResponseEntity<?> addOrdonnance (Long idUtilisateur) {
        if (accessControlService.hasAccess(new Ordonnance(), idUtilisateur, EAccessLevel.WRITE)) {
            return ResponseHandler.generateResponse("", HttpStatus.OK, null);
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }

    public ResponseEntity<?> updateOrdonnance (Long idUtilisateur) {
        if (accessControlService.hasAccess(new Ordonnance(), idUtilisateur, EAccessLevel.UPDATE)) {
            return ResponseHandler.generateResponse("", HttpStatus.OK, null);
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }

    public ResponseEntity<?> deleteOrdonnance (Long id , Long idUtilisateur) {
        if (accessControlService.hasAccess(new Ordonnance(), idUtilisateur, EAccessLevel.DELETE)) {
            return ResponseHandler.generateResponse("", HttpStatus.OK, null);
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }
}
