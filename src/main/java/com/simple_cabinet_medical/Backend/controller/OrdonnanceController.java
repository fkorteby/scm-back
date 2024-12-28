package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.service.OrdonnanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrdonnanceController {

    private final OrdonnanceService ordonnanceService;

    public OrdonnanceController(OrdonnanceService ordonnanceService) {
        this.ordonnanceService = ordonnanceService;
    }

    @GetMapping
    public ResponseEntity getAllOrdonnance () {
        return null;
    }

    @PostMapping
    public ResponseEntity createOrdonnance () {
        return null;
    }

    @PutMapping
    public ResponseEntity updateOrdonnance () {
        return null;
    }
}
