package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.model.EAccessLevel;
import com.simple_cabinet_medical.Backend.model.Patient;
import com.simple_cabinet_medical.Backend.payload.request.AddPatientRequest;
import com.simple_cabinet_medical.Backend.repository.PatientRepository;
import com.simple_cabinet_medical.Backend.utils.ResponseHandler;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AccessControlService accessControlService;

    public PatientService(PatientRepository patientRepository, AccessControlService accessControlService) {
        this.patientRepository = patientRepository;
        this.accessControlService = accessControlService;
    }

    public ResponseEntity<?> addPatient (AddPatientRequest data) {
        if (accessControlService.hasAccess(new Patient(), data.getIdUtilisateur(), EAccessLevel.WRITE)) {
            return ResponseHandler.generateResponse("", HttpStatus.OK, null);
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }

    public ResponseEntity<?> deletePatient (Long id, Long idUtilisateur) {
        if (accessControlService.hasAccess(new Patient(), idUtilisateur, EAccessLevel.DELETE)) {
            return ResponseHandler.generateResponse("", HttpStatus.OK, null);
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }

    public ResponseEntity<?> updatePatient (AddPatientRequest data) {
        if (accessControlService.hasAccess(new Patient(), data.getIdUtilisateur(), EAccessLevel.UPDATE)) {
            return ResponseHandler.generateResponse("", HttpStatus.OK, null);
        } else {
            return ResponseHandler.generateResponse("Accès refusé. Veuillez contacter l'administrateur.", HttpStatus.FORBIDDEN, null);
        }
    }

}
