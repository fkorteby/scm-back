package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.model.Conduite;
import com.simple_cabinet_medical.Backend.model.EAccessLevel;
import com.simple_cabinet_medical.Backend.model.Forme;
import com.simple_cabinet_medical.Backend.payload.request.AddFormeRequest;
import com.simple_cabinet_medical.Backend.repository.FormeRepository;
import com.simple_cabinet_medical.Backend.utils.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class FormeService {

    private final FormeRepository formeRepository;
    private final AccessControlService accessControlService;

    public FormeService(FormeRepository formeRepository, AccessControlService accessControlService) {
        this.formeRepository = formeRepository;
        this.accessControlService = accessControlService;
    }

    public ResponseEntity<?> addForme (AddFormeRequest data) {
        if (accessControlService.hasAccess(new Forme(), data.getIdUtilisateur(), EAccessLevel.WRITE)) {
            if (formeRepository.findByForme(data.getForme()).isPresent()) {
                return ResponseHandler.generateResponse("Forme Existe déja", HttpStatus.CONFLICT, null);
            } else {
                Forme forme = formeRepository.save(new Forme(data.getForme(), data.getAbreviation(), data.getIdUtilisateur()));
                return ResponseHandler.generateResponse("Forme bien enregistré", HttpStatus.CREATED,forme);
            }
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }

    public ResponseEntity<?> deleteForme (Long idForme, Long idUtilisateur) {
        if (accessControlService.hasAccess(new Forme(), idUtilisateur, EAccessLevel.DELETE)) {
            if (formeRepository.findById(idForme).isPresent()) {
                formeRepository.deleteById(idForme);
                return ResponseHandler.generateResponse("La Forme a été supprimée avec succès.", HttpStatus.OK, null);
            } else {
                return ResponseHandler.generateResponse("La forme n'existe pas", HttpStatus.NOT_FOUND, null);
            }
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }

    public ResponseEntity<?> updateForme (AddFormeRequest data, Long idForme) {
        if (accessControlService.hasAccess(new Forme(), data.getIdUtilisateur(), EAccessLevel.DELETE)) {
            if (formeRepository.findById(idForme).isPresent()) {
                Forme forme = formeRepository.findById(idForme).get();
                forme.setForme(data.getForme());
                forme.setAbreviation(data.getAbreviation());
                forme = formeRepository.save(forme);
                return ResponseHandler.generateResponse("La forme été mise à jour avec succès", HttpStatus.OK, forme);
            } else {
                return ResponseHandler.generateResponse("La forme n'existe pas", HttpStatus.NOT_FOUND, null);
            }
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }

    public ResponseEntity<?> getAllForme (Long idUtilisateur) {
        if (accessControlService.hasAccess(new Forme(), idUtilisateur, EAccessLevel.DELETE)) {
            List<Forme> list = formeRepository.findAll();
            return ResponseHandler.generateResponse("Liste des formes récupérée avec succès.",HttpStatus.OK, list);
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }

    public ResponseEntity<?> findAllFormeByIdUtilisateur (Long idUtilisateur) {
        if (accessControlService.hasAccess(new Forme(), idUtilisateur, EAccessLevel.DELETE)) {
            List<Forme> list = formeRepository.findAllByIdUtilisateur(idUtilisateur);
            return ResponseHandler.generateResponse("Liste des formes récupérée avec succès.", HttpStatus.OK, list);
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }
}
