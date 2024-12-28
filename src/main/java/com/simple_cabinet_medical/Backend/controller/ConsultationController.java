package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.payload.request.AddConsultationRequest;
import com.simple_cabinet_medical.Backend.service.ConduiteService;
import com.simple_cabinet_medical.Backend.service.ConsultationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ConsultationController {

    private final ConsultationService consultationService;

    public ConsultationController(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    @PostMapping("/consultations")
    public ResponseEntity<?> createConsultation () {
        return null;
    }

    @GetMapping("/consultations")
    public ResponseEntity<?> getAllConsultation () {
        return null;
    }

    @GetMapping("/consultations/{idConsultation}")
    public ResponseEntity<?> getConsultation (@PathVariable Long idConsultation) {
        return null;
    }


    @PutMapping("/consultations/{idConsultation}")
    public ResponseEntity<?> updateConsultation (@PathVariable Long idConsultation , @RequestBody AddConsultationRequest data) {
        return null;
    }



}
