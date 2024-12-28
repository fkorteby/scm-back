package com.simple_cabinet_medical.Backend.service;

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

    public ParacliniqueService(ParacliniqueRepository paracliniqueRepository) {
        this.paracliniqueRepository = paracliniqueRepository;
    }

    public ResponseEntity<?> addParaclinique (AddParacliniqueRequest data) {
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
    }

    public ResponseEntity<?> updateParaclinique (AddParacliniqueRequest data, Long id) {
        if (paracliniqueRepository.findById(id).isPresent()) {
            return ResponseHandler.generateResponse("", HttpStatus.NOT_FOUND, null);
        } else {
            Paraclinique paraclinique = paracliniqueRepository.findById(id).get();
            paraclinique.setExamen(data.getExamen());
            paraclinique.setType(data.getType());
            return ResponseHandler.generateResponse("", HttpStatus.OK, null);
        }
    }

    public ResponseEntity<?> deleteParaclinique (Long id) {
        if (paracliniqueRepository.findById(id).isPresent()) {
            paracliniqueRepository.deleteById(id);
            return ResponseHandler.generateResponse("", HttpStatus.OK, null);
        } else {
            return ResponseHandler.generateResponse("", HttpStatus.NOT_FOUND, null);
        }
    }

    public ResponseEntity<?> getAllParaclinique () {
        List<Paraclinique> list =  paracliniqueRepository.findAll();
        return ResponseHandler.generateResponse("", HttpStatus.OK, list);
    }

    public ResponseEntity<?> getAllParacliniqueByIdUtilisateur (Long idUtilisateur) {
        List<Paraclinique> list = paracliniqueRepository.findAllByIdUtilisateur(idUtilisateur);
        return ResponseHandler.generateResponse("", HttpStatus.OK, list);
    }
}
