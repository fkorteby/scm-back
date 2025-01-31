package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.model.Duree;
import com.simple_cabinet_medical.Backend.model.EAccessLevel;
import com.simple_cabinet_medical.Backend.model.Motif;
import com.simple_cabinet_medical.Backend.payload.request.AddParametreRequest;
import com.simple_cabinet_medical.Backend.repository.DureeRepository;
import com.simple_cabinet_medical.Backend.utils.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DureeService {

    private final DureeRepository dureeRepository;

    private final AccessControlService accessControlService;

    public DureeService(DureeRepository dureeRepository, AccessControlService accessControlService) {
        this.dureeRepository = dureeRepository;
        this.accessControlService = accessControlService;
    }

    public ResponseEntity<?> add (AddParametreRequest data) {
        System.out.println(" Access Level value : " + accessControlService.hasAccess(new Duree(), data.getIdUtilisateur(), EAccessLevel.WRITE));
        if (accessControlService.hasAccess(new Duree(), data.getIdUtilisateur(), EAccessLevel.WRITE)) {
            if (dureeRepository.findByDuree(data.getParametre()).isPresent()) {
                return ResponseHandler.generateResponse("Parametre existe deja", HttpStatus.CONFLICT, null);
            } else {
                Duree duree = new Duree(data.getParametre(), data.getIdUtilisateur());
                duree = dureeRepository.save(duree);
                return ResponseHandler.generateResponse("",HttpStatus.CREATED, duree);
            }
        } else {
            return ResponseHandler.generateResponse("Accès non autorisé", HttpStatus.FORBIDDEN, null);
        }

    }

    public ResponseEntity<?> update (AddParametreRequest data, Long id) {
        if (accessControlService.hasAccess(new Duree(), data.getIdUtilisateur(), EAccessLevel.UPDATE)) {
            if (dureeRepository.findById(id).isPresent()) {
                Duree duree = dureeRepository.findById(id).get();
                duree.setDuree(data.getParametre());
                duree = dureeRepository.save(duree);
                return ResponseHandler.generateResponse("", HttpStatus.OK, duree);
            } else {
                return ResponseHandler.generateResponse("",HttpStatus.NOT_FOUND, null);
            }
        } else {
            return ResponseHandler.generateResponse("Accés non autorisé", HttpStatus.FORBIDDEN, null);
        }
    }

    public ResponseEntity<?> delete (Long id, Long idUtilisateur) {
        if (accessControlService.hasAccess(new Duree(), idUtilisateur, EAccessLevel.DELETE)) {
            if (dureeRepository.findById(id).isPresent()) {
                dureeRepository.deleteById(id);
                return ResponseHandler.generateResponse("", HttpStatus.OK, null);
            } else {
                return ResponseHandler.generateResponse("",HttpStatus.NOT_FOUND, null);
            }
        } else {
            return ResponseHandler.generateResponse("Accés non autorisé", HttpStatus.FORBIDDEN, null);
        }

    }

    public ResponseEntity<?> getAll (Long idUtilisateur) {
        if (accessControlService.hasAccess(new Duree(), idUtilisateur, EAccessLevel.READ)) {
            List<Duree> list = dureeRepository.findAll();
            return ResponseHandler.generateResponse("", HttpStatus.OK, list);
        } else {
            return ResponseHandler.generateResponse("Accés non autorisé",HttpStatus.FORBIDDEN, null);
        }

    }

    public ResponseEntity<?> getAllByIdUtilisateur (Long id) {
        if (accessControlService.hasAccess(new Duree(), id, EAccessLevel.READ)) {
            List<Duree> list = dureeRepository.findAllByIdUtilisateur(id);
            return ResponseHandler.generateResponse("", HttpStatus.OK, list);
        } else {
            return ResponseHandler.generateResponse("Accés non autorisé",HttpStatus.FORBIDDEN, null);
        }
    }
}
