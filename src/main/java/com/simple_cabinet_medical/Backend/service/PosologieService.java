package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.model.Motif;
import com.simple_cabinet_medical.Backend.model.Posologie;
import com.simple_cabinet_medical.Backend.payload.request.AddParametreRequest;
import com.simple_cabinet_medical.Backend.repository.PosologieRepository;
import com.simple_cabinet_medical.Backend.utils.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PosologieService {

    private final PosologieRepository posologieRepository;

    public PosologieService(PosologieRepository posologieRepository) {
        this.posologieRepository = posologieRepository;
    }

    public ResponseEntity<?> add (AddParametreRequest data) {
        if (posologieRepository.findByPosologie(data.getParametre()).isPresent()) {
            return ResponseHandler.generateResponse("", HttpStatus.CONFLICT, null);
        } else {
            Posologie posologie = new Posologie(data.getParametre(), data.getIdUtilisateur());
            posologie = posologieRepository.save(posologie);
            return ResponseHandler.generateResponse("",HttpStatus.CREATED, posologie);
        }
    }

    public ResponseEntity<?> update (AddParametreRequest data, Long id) {
        if (posologieRepository.findById(id).isPresent()) {
            Posologie posologie = posologieRepository.findById(id).get();
            posologie.setPosologie(data.getParametre());
            posologieRepository.save(posologie);
            return ResponseHandler.generateResponse("", HttpStatus.OK, null);
        } else {
            return ResponseHandler.generateResponse("",HttpStatus.NOT_FOUND, null);
        }
    }

    public ResponseEntity<?> delete (Long id) {
        if (posologieRepository.findById(id).isPresent()) {
            posologieRepository.deleteById(id);
            return ResponseHandler.generateResponse("", HttpStatus.OK, null);
        } else {
            return ResponseHandler.generateResponse("",HttpStatus.NOT_FOUND, null);
        }
    }

    public ResponseEntity<?> getAll () {
        List<Posologie> list = posologieRepository.findAll();
        return ResponseHandler.generateResponse("", HttpStatus.OK, list);
    }

    public ResponseEntity<?> getAllByIdUtilisateur (Long id) {
        List<Posologie> list = posologieRepository.findAllByIdUtilisateur(id);
        return ResponseHandler.generateResponse("",HttpStatus.OK, list);
    }
}
