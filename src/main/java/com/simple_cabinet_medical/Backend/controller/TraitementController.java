package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.service.TraitementService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TraitementController {

    private final TraitementService traitementService;

    public TraitementController(TraitementService traitementService) {
        this.traitementService = traitementService;
    }
}
