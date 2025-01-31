package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.model.EAccessLevel;
import com.simple_cabinet_medical.Backend.model.Local;
import com.simple_cabinet_medical.Backend.payload.request.AddLocalRequest;
import com.simple_cabinet_medical.Backend.repository.LocalRepository;
import com.simple_cabinet_medical.Backend.utils.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class LocalService {

    private final LocalRepository localRepository;
    private final AccessControlService accessControlService;

    public LocalService(LocalRepository localRepository, AccessControlService accessControlService) {
        this.localRepository = localRepository;
        this.accessControlService = accessControlService;
    }

    public ResponseEntity<?> addLocal (AddLocalRequest data) {
        if (accessControlService.hasAccess(new Local(), data.getIdUtilisateur(), EAccessLevel.WRITE)) {
            return ResponseHandler.generateResponse("", HttpStatus.OK, null);
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }

    public ResponseEntity<?> deleteLocal (Long id, Long idUtilisateur) {
        if (accessControlService.hasAccess(new Local(), idUtilisateur, EAccessLevel.DELETE)) {
            return ResponseHandler.generateResponse("", HttpStatus.OK, null);
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }

    public ResponseEntity<?> updateLocal (AddLocalRequest data) {
        if (accessControlService.hasAccess(new Local(), data.getIdUtilisateur(), EAccessLevel.UPDATE)) {
            return ResponseHandler.generateResponse("", HttpStatus.OK, null);
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }

}
