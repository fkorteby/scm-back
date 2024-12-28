package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.model.Medicament;
import com.simple_cabinet_medical.Backend.payload.request.AddMedicamentRequest;
import com.simple_cabinet_medical.Backend.repository.MedicamentRepository;
import com.simple_cabinet_medical.Backend.utils.ResponseHandler;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicamentService {

    private final MedicamentRepository medicamentRepository;

    public MedicamentService(MedicamentRepository medicamentRepository) {
        this.medicamentRepository = medicamentRepository;
    }

    public ResponseEntity<?> addMedicament (AddMedicamentRequest data) {
        if (medicamentRepository.findByNomCommerciale(data.getNomCommerciale()).isPresent()) {
            return ResponseHandler.generateResponse("", HttpStatus.CONFLICT, null);
        } else {
            Medicament medicament = new Medicament(
                    data.getNomCommerciale(),
                    data.getDci(),
                    data.getDosage(),
                    data.getConditionnement(),
                    data.getForme(),
                    data.getIdUtilisateur()
            );
            medicament = medicamentRepository.save(medicament);
            return ResponseHandler.generateResponse("", HttpStatus.CREATED, medicament);
        }
    }

    public ResponseEntity<?> deleteMedicament (Long idMedicament) {
        if (medicamentRepository.findById(idMedicament).isPresent()) {
            medicamentRepository.deleteById(idMedicament);
            return ResponseHandler.generateResponse("", HttpStatus.OK, null);
        } else {
            return ResponseHandler.generateResponse("", HttpStatus.NOT_FOUND, null);
        }
    }

    public ResponseEntity<?> updateMedicament (AddMedicamentRequest data, Long idMedicament) {
        if (medicamentRepository.findById(idMedicament).isPresent()) {
            Medicament medicament = medicamentRepository.findById(idMedicament).get();
            medicament.setConditionnement(data.getConditionnement());
            medicament.setNomCommerciale(data.getNomCommerciale());
            medicament.setForme(data.getForme());
            medicament.setDci(data.getDci());
            medicament.setDosage(data.getDosage());
            medicament = medicamentRepository.save(medicament);

            return ResponseHandler.generateResponse("", HttpStatus.OK, medicament);
        } else {
            return ResponseHandler.generateResponse("", HttpStatus.NOT_FOUND, null);
        }
    }

    public ResponseEntity<?> getAllMedicament () {
        List <Medicament> list =  medicamentRepository.findAll();
        return ResponseHandler.generateResponse("", HttpStatus.OK, list);
    }

    public ResponseEntity<?> getAllMedicamentByIdUtilisateur(Long idUtilisateur) {
        List<Medicament> list = medicamentRepository.findAllByIdUtilisateur(idUtilisateur);
        return ResponseHandler.generateResponse("",HttpStatus.OK, list);
    }
}
