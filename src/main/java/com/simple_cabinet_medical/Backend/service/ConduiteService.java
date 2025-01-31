package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.model.Conduite;
import com.simple_cabinet_medical.Backend.model.Duree;
import com.simple_cabinet_medical.Backend.model.EAccessLevel;
import com.simple_cabinet_medical.Backend.model.Motif;
import com.simple_cabinet_medical.Backend.payload.request.AddParametreRequest;
import com.simple_cabinet_medical.Backend.repository.ConduiteRepository;
import com.simple_cabinet_medical.Backend.utils.ResponseHandler;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConduiteService {

    private final ConduiteRepository conduiteRepository;
    private final AccessControlService accessControlService;

    public ConduiteService(ConduiteRepository conduiteRepository, AccessControlService accessControlService) {
        this.conduiteRepository = conduiteRepository;
        this.accessControlService = accessControlService;
    }

    public ResponseEntity<?> add (AddParametreRequest data) {
        if (accessControlService.hasAccess(new Conduite(), data.getIdUtilisateur(), EAccessLevel.WRITE)) {
            if (conduiteRepository.findByConduite(data.getParametre()).isPresent()) {
                return ResponseHandler.generateResponse("", HttpStatus.CONFLICT, null);
            } else {
                Conduite conduite = new Conduite(data.getParametre(), data.getIdUtilisateur());
                conduite = conduiteRepository.save(conduite);
                return ResponseHandler.generateResponse("",HttpStatus.CREATED, conduite);
            }
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }

    public ResponseEntity<?> update (AddParametreRequest data, Long id) {
        if (accessControlService.hasAccess(new Conduite(), data.getIdUtilisateur(), EAccessLevel.UPDATE)) {
            if (conduiteRepository.findById(id).isPresent()) {
                Conduite conduite = conduiteRepository.findById(id).get();
                conduite.setConduite(data.getParametre());
                conduite = conduiteRepository.save(conduite);
                return ResponseHandler.generateResponse("", HttpStatus.OK, conduite);
            } else {
                return ResponseHandler.generateResponse("",HttpStatus.NOT_FOUND, null);
            }
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }

    public ResponseEntity<?> delete (Long id, Long idUtilisateur) {

        if (accessControlService.hasAccess(new Conduite(), idUtilisateur, EAccessLevel.DELETE)) {
            if (conduiteRepository.findById(id).isPresent()) {
                conduiteRepository.deleteById(id);
                return ResponseHandler.generateResponse("", HttpStatus.CONFLICT, null);
            } else {
                return ResponseHandler.generateResponse("",HttpStatus.NOT_FOUND, null);
            }
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }

    }

    public ResponseEntity<?> getAll (Long idUtilisateur) {
        if (accessControlService.hasAccess(new Conduite(), idUtilisateur, EAccessLevel.READ)) {
            List<Conduite> list = conduiteRepository.findAll();
            return ResponseHandler.generateResponse("", HttpStatus.OK, list);
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }

    public ResponseEntity<?> getAllByIdUtilisateur (Long id) {
        if (accessControlService.hasAccess(new Conduite(), id, EAccessLevel.READ)) {
            List<Conduite> list = conduiteRepository.findAllByIdUtilisateur(id);
            return ResponseHandler.generateResponse("", HttpStatus.OK, list);
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }
}
