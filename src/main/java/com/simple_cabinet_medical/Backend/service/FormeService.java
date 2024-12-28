package com.simple_cabinet_medical.Backend.service;

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

    public FormeService(FormeRepository formeRepository) {
        this.formeRepository = formeRepository;
    }

    public ResponseEntity<?> addForme (AddFormeRequest data) {
        if (formeRepository.findByForme(data.getForme()).isPresent()) {
            return ResponseHandler.generateResponse("Forme Existe déja", HttpStatus.CONFLICT, null);
        } else {
            Forme forme = formeRepository.save(new Forme(data.getForme(), data.getAbreviation(), data.getIdUtilisateur()));
            return ResponseHandler.generateResponse("Forme bien enregistré", HttpStatus.CREATED,forme);
        }
    }

    public ResponseEntity<?> deleteForme (Long idForme) {
        if (formeRepository.findById(idForme).isPresent()) {
            formeRepository.deleteById(idForme);
            return ResponseHandler.generateResponse("La Forme a été supprimée avec succès.", HttpStatus.OK, null);
        } else {
            return ResponseHandler.generateResponse("La forme n'existe pas", HttpStatus.NOT_FOUND, null);
        }
    }

    public ResponseEntity<?> updateForme (AddFormeRequest data, Long idForme) {
        if (formeRepository.findById(idForme).isPresent()) {
            Forme forme = formeRepository.findById(idForme).get();
            forme.setForme(data.getForme());
            forme.setAbreviation(data.getAbreviation());
            forme = formeRepository.save(forme);
            return ResponseHandler.generateResponse("La forme été mise à jour avec succès", HttpStatus.OK, forme);
        } else {
            return ResponseHandler.generateResponse("La forme n'existe pas", HttpStatus.NOT_FOUND, null);
        }
    }

    public ResponseEntity<?> getAllForme () {
        List<Forme> list = formeRepository.findAll();
        return ResponseHandler.generateResponse("Liste des formes récupérée avec succès.",HttpStatus.OK, list);
    }

    public ResponseEntity findAllFormeByIdUtilisateur (Long idUtilisateur) {
        List<Forme> list = formeRepository.findAllByIdUtilisateur(idUtilisateur);
        return ResponseHandler.generateResponse("Liste des formes récupérée avec succès.", HttpStatus.OK, list);
    }
}
