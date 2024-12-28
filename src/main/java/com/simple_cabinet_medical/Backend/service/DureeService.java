package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.model.Duree;
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

    public DureeService(DureeRepository dureeRepository) {
        this.dureeRepository = dureeRepository;
    }

    public ResponseEntity<?> add (AddParametreRequest data) {
        if (dureeRepository.findByDuree(data.getParametre()).isPresent()) {
            return ResponseHandler.generateResponse("", HttpStatus.CONFLICT, null);
        } else {
            Duree duree = new Duree(data.getParametre(), data.getIdUtilisateur());
            dureeRepository.save(duree);
            return ResponseHandler.generateResponse("",HttpStatus.CREATED, null);
        }
    }

    public ResponseEntity<?> update (AddParametreRequest data, Long id) {
        if (dureeRepository.findById(id).isPresent()) {
            Duree duree = dureeRepository.findById(id).get();
            duree.setDuree(data.getParametre());
            duree = dureeRepository.save(duree);
            return ResponseHandler.generateResponse("", HttpStatus.OK, duree);
        } else {
            return ResponseHandler.generateResponse("",HttpStatus.NOT_FOUND, null);
        }
    }

    public ResponseEntity<?> delete (Long id) {
        if (dureeRepository.findById(id).isPresent()) {
            dureeRepository.deleteById(id);
            return ResponseHandler.generateResponse("", HttpStatus.OK, null);
        } else {
            return ResponseHandler.generateResponse("",HttpStatus.NOT_FOUND, null);
        }
    }

    public ResponseEntity<?> getAll () {
        List<Duree> list = dureeRepository.findAll();
        return ResponseHandler.generateResponse("", HttpStatus.OK, list);
    }

    public ResponseEntity<?> getAllByIdUtilisateur (Long id) {
        List<Duree> list = dureeRepository.findAllByIdUtilisateur(id);
        return ResponseHandler.generateResponse("", HttpStatus.OK, list);
    }
}
