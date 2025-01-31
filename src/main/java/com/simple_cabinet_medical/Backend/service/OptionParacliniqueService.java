package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.model.EAccessLevel;
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

    private final AccessControlService accessControlService;

    public OptionParacliniqueService(OptionParacliniqueRepository optionParacliniqueRepository, AccessControlService accessControlService) {
        this.optionParacliniqueRepository = optionParacliniqueRepository;
        this.accessControlService = accessControlService;
    }

    public ResponseEntity<?> add (AddParametreRequest data) {
        if (accessControlService.hasAccess(new OptionParaclinique(), data.getIdUtilisateur(), EAccessLevel.WRITE)) {
            if (optionParacliniqueRepository.findByOption(data.getParametre()).isPresent()) {
                return ResponseHandler.generateResponse("", HttpStatus.CONFLICT, null);
            } else {
                OptionParaclinique option = new OptionParaclinique(data.getParametre(), data.getIdUtilisateur());
                option = optionParacliniqueRepository.save(option);
                return ResponseHandler.generateResponse("",HttpStatus.CREATED, option);
            }
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }

    public ResponseEntity<?> update (AddParametreRequest data, Long id) {
        if (accessControlService.hasAccess(new OptionParaclinique(), data.getIdUtilisateur(), EAccessLevel.UPDATE)) {
            if (optionParacliniqueRepository.findById(id).isPresent()) {
                OptionParaclinique option = optionParacliniqueRepository.findById(id).get();
                option.setOption(data.getParametre());
                option = optionParacliniqueRepository.save(option);
                return ResponseHandler.generateResponse("", HttpStatus.OK, option);
            } else {

                return ResponseHandler.generateResponse("",HttpStatus.NOT_FOUND, null);
            }
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }

    public ResponseEntity<?> delete (Long id, Long idUtilisateur) {
        if (accessControlService.hasAccess(new OptionParaclinique(), idUtilisateur, EAccessLevel.DELETE)) {
            if (optionParacliniqueRepository.findById(id).isPresent()) {
                optionParacliniqueRepository.deleteById(id);
                return ResponseHandler.generateResponse("", HttpStatus.OK, null);
            } else {
                return ResponseHandler.generateResponse("",HttpStatus.NOT_FOUND, null);
            }
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }

    public ResponseEntity<?> getAll (Long idUtilisateur) {
        if (accessControlService.hasAccess(new OptionParaclinique(), idUtilisateur, EAccessLevel.READ)) {
            List<OptionParaclinique> list = optionParacliniqueRepository.findAll();
            return ResponseHandler.generateResponse("", HttpStatus.OK, list);
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }

    public ResponseEntity<?> getAllByIdUtilisateur (Long id) {
        if (accessControlService.hasAccess(new OptionParaclinique(), id, EAccessLevel.READ)) {
            List<OptionParaclinique> list = optionParacliniqueRepository.findAllByIdUtilisateur(id);
            return ResponseHandler.generateResponse("", HttpStatus.OK, list);
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }
}
