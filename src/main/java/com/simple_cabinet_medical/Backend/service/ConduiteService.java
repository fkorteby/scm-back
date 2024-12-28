package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.model.Conduite;
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

    public ConduiteService(ConduiteRepository conduiteRepository) {
        this.conduiteRepository = conduiteRepository;
    }

    public ResponseEntity<?> add (AddParametreRequest data) {
        if (conduiteRepository.findByConduite(data.getParametre()).isPresent()) {
            return ResponseHandler.generateResponse("", HttpStatus.CONFLICT, null);
        } else {
            Conduite conduite = new Conduite(data.getParametre(), data.getIdUtilisateur());
            conduite = conduiteRepository.save(conduite);
            return ResponseHandler.generateResponse("",HttpStatus.CREATED, conduite);
        }
    }

    public ResponseEntity<?> update (AddParametreRequest data, Long id) {
        if (conduiteRepository.findById(id).isPresent()) {
            Conduite conduite = conduiteRepository.findById(id).get();
            conduite.setConduite(data.getParametre());
            conduite = conduiteRepository.save(conduite);
            return ResponseHandler.generateResponse("", HttpStatus.OK, conduite);
        } else {
            return ResponseHandler.generateResponse("",HttpStatus.NOT_FOUND, null);
        }
    }

    public ResponseEntity<?> delete (Long id) {
        if (conduiteRepository.findById(id).isPresent()) {
            conduiteRepository.deleteById(id);
            return ResponseHandler.generateResponse("", HttpStatus.CONFLICT, null);
        } else {
            return ResponseHandler.generateResponse("",HttpStatus.NOT_FOUND, null);
        }
    }

    public ResponseEntity<?> getAll () {
        List<Conduite> list = conduiteRepository.findAll();
        return ResponseHandler.generateResponse("", HttpStatus.OK, list);
    }

    public ResponseEntity<?> getAllByIdUtilisateur (Long id) {
        List<Conduite> list = conduiteRepository.findAllByIdUtilisateur(id);
        return ResponseHandler.generateResponse("", HttpStatus.OK, list);
    }
}
