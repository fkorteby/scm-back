package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.model.Motif;
import com.simple_cabinet_medical.Backend.model.OptionParaclinique;
import com.simple_cabinet_medical.Backend.payload.request.AddParametreRequest;
import com.simple_cabinet_medical.Backend.repository.OptionParacliniqueRepository;
import com.simple_cabinet_medical.Backend.utils.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OptionParacliniqueService {

    private final OptionParacliniqueRepository optionParacliniqueRepository;

    public OptionParacliniqueService(OptionParacliniqueRepository optionParacliniqueRepository) {
        this.optionParacliniqueRepository = optionParacliniqueRepository;
    }

    public ResponseEntity<?> add (AddParametreRequest data) {
        if (optionParacliniqueRepository.findByOption(data.getParametre()).isPresent()) {
            return ResponseHandler.generateResponse("", HttpStatus.CONFLICT, null);
        } else {
            OptionParaclinique option = new OptionParaclinique(data.getParametre(), data.getIdUtilisateur());
            option = optionParacliniqueRepository.save(option);
            return ResponseHandler.generateResponse("",HttpStatus.CREATED, option);
        }
    }

    public ResponseEntity<?> update (AddParametreRequest data, Long id) {
        if (optionParacliniqueRepository.findById(id).isPresent()) {
            OptionParaclinique option = optionParacliniqueRepository.findById(id).get();
            option.setOption(data.getParametre());
            option = optionParacliniqueRepository.save(option);
            return ResponseHandler.generateResponse("", HttpStatus.OK, option);
        } else {

            return ResponseHandler.generateResponse("",HttpStatus.NOT_FOUND, null);
        }
    }

    public ResponseEntity<?> delete (Long id) {
        if (optionParacliniqueRepository.findById(id).isPresent()) {
            optionParacliniqueRepository.deleteById(id);
            return ResponseHandler.generateResponse("", HttpStatus.OK, null);
        } else {

            return ResponseHandler.generateResponse("",HttpStatus.NOT_FOUND, null);
        }
    }

    public ResponseEntity<?> getAll () {
        List<OptionParaclinique> list = optionParacliniqueRepository.findAll();
        return ResponseHandler.generateResponse("", HttpStatus.OK, list);
    }

    public ResponseEntity<?> getAllByIdUtilisateur (Long id) {
        List<OptionParaclinique> list = optionParacliniqueRepository.findAllByIdUtilisateur(id);
        return ResponseHandler.generateResponse("", HttpStatus.OK, list);
    }
}
