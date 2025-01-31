package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.model.EAccessLevel;
import com.simple_cabinet_medical.Backend.model.Paraclinique;
import com.simple_cabinet_medical.Backend.payload.request.AddMedicamentRequest;
import com.simple_cabinet_medical.Backend.payload.request.AddParacliniqueRequest;
import com.simple_cabinet_medical.Backend.repository.ParacliniqueRepository;
import com.simple_cabinet_medical.Backend.utils.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParacliniqueService {

    private final ParacliniqueRepository paracliniqueRepository;
    private final AccessControlService accessControlService;

    public ParacliniqueService(ParacliniqueRepository paracliniqueRepository, AccessControlService accessControlService) {
        this.paracliniqueRepository = paracliniqueRepository;
        this.accessControlService = accessControlService;
    }

    public ResponseEntity<?> addParaclinique (AddParacliniqueRequest data) {
        if (accessControlService.hasAccess(new Paraclinique(), data.getIdUtilisateur(), EAccessLevel.WRITE)) {
            if (paracliniqueRepository.findByExamen(data.getExamen()).isPresent()) {
                return ResponseHandler.generateResponse("", HttpStatus.NOT_FOUND, null);
            } else {
                Paraclinique paraclinique = new Paraclinique(
                        data.getExamen(),
                        data.getType(),
                        data.getIdUtilisateur()
                );
                paraclinique = paracliniqueRepository.save(paraclinique);
                return ResponseHandler.generateResponse("", HttpStatus.CREATED, paraclinique);
            }
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }

    public ResponseEntity<?> updateParaclinique (AddParacliniqueRequest data, Long id) {
        if (accessControlService.hasAccess(new Paraclinique(), data.getIdUtilisateur(), EAccessLevel.UPDATE)) {
            if (paracliniqueRepository.findById(id).isPresent()) {
                return ResponseHandler.generateResponse("", HttpStatus.NOT_FOUND, null);
            } else {
                Paraclinique paraclinique = paracliniqueRepository.findById(id).get();
                paraclinique.setExamen(data.getExamen());
                paraclinique.setType(data.getType());
                return ResponseHandler.generateResponse("", HttpStatus.OK, null);
            }
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }

    public ResponseEntity<?> deleteParaclinique (Long id, Long idUtilisateur) {
        if (accessControlService.hasAccess(new Paraclinique(), idUtilisateur, EAccessLevel.DELETE)) {
            if (paracliniqueRepository.findById(id).isPresent()) {
                paracliniqueRepository.deleteById(id);
                return ResponseHandler.generateResponse("", HttpStatus.OK, null);
            } else {
                return ResponseHandler.generateResponse("", HttpStatus.NOT_FOUND, null);
            }
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }

    public ResponseEntity<?> getAllParaclinique (Long idUtilisateur) {
        if (accessControlService.hasAccess(new Paraclinique(), idUtilisateur, EAccessLevel.READ)) {
            List<Paraclinique> list =  paracliniqueRepository.findAll();
            return ResponseHandler.generateResponse("", HttpStatus.OK, list);
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }

    public ResponseEntity<?> getAllParacliniqueByIdUtilisateur (Long idUtilisateur) {
        if (accessControlService.hasAccess(new Paraclinique(), idUtilisateur, EAccessLevel.READ)) {
            List<Paraclinique> list = paracliniqueRepository.findAllByIdUtilisateur(idUtilisateur);
            return ResponseHandler.generateResponse("", HttpStatus.OK, list);
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }
}
