package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.model.Motif;
import com.simple_cabinet_medical.Backend.payload.request.AddParametreRequest;
import com.simple_cabinet_medical.Backend.repository.MotifRepository;
import com.simple_cabinet_medical.Backend.utils.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MotifService {

    private final MotifRepository motifRepository;

    public MotifService(MotifRepository motifRepository) {
        this.motifRepository = motifRepository;
    }

    public ResponseEntity<?> addMotif (AddParametreRequest data) {
        if (motifRepository.findByMotif(data.getParametre()).isPresent()) {
            return ResponseHandler.generateResponse("", HttpStatus.CONFLICT, null);
        } else {
            Motif motif = new Motif(data.getParametre(), data.getIdUtilisateur());
            motifRepository.save(motif);
            return ResponseHandler.generateResponse("",HttpStatus.CREATED, null);
        }
    }

    public ResponseEntity<?> updateMotif (AddParametreRequest data, Long id) {
        if (motifRepository.findById(id).isPresent()) {
            Motif motif = motifRepository.findById(id).get();
            motif.setMotif(data.getParametre());
            motif = motifRepository.save(motif);
            return ResponseHandler.generateResponse("", HttpStatus.OK, motif);
        } else {
            return ResponseHandler.generateResponse("",HttpStatus.NOT_FOUND, null);
        }
    }

    public ResponseEntity<?> deleteMotif (Long id) {
        if (motifRepository.findById(id).isPresent()) {
            motifRepository.deleteById(id);
            return ResponseHandler.generateResponse("", HttpStatus.OK, null);
        } else {
            return ResponseHandler.generateResponse("",HttpStatus.NOT_FOUND, null);
        }
    }

    public ResponseEntity<?> getAllMotif () {
        List<Motif> list = motifRepository.findAll();
        return ResponseHandler.generateResponse("", HttpStatus.OK, list);
    }

    public ResponseEntity<?> getAllMotifByIdUtilisateur (Long id) {
        List<Motif> list = motifRepository.findAllByIdUtilisateur(id);
        return ResponseHandler.generateResponse("", HttpStatus.OK, list);
    }
}
