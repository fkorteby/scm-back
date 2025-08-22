package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.Dto.PatientResponseDto;
import com.simple_cabinet_medical.Backend.service.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/rendezvous-today")
    public ResponseEntity<Page<PatientResponseDto>> findPatientsWithRendezVousToday(
            Long idClient,
            Date date,
            int page,
            int size) {
        Page<PatientResponseDto> patients = patientService.findPatientsWithRendezVousToday(idClient, date, page, size);
        return ResponseEntity.ok(patients);
    }
}
