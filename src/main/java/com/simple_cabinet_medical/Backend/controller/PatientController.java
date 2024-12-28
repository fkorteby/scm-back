package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.payload.request.AddPatientRequest;
import com.simple_cabinet_medical.Backend.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/patients")
    public ResponseEntity<?> getAllPatient () {
        return null;
    }

    @PostMapping("/patients")
    public ResponseEntity<?> createPatient () {
        return null;
    }

    @GetMapping("/patients/{idPatient}")
    public ResponseEntity<?> updatePateint (@PathVariable Long idPatient, @RequestBody AddPatientRequest data) {
        return null;
    }

    @DeleteMapping("/patients/{idPatient}")
    public ResponseEntity<?> deletePatient (@PathVariable Long idPatient) {
        return null;
    }
}
