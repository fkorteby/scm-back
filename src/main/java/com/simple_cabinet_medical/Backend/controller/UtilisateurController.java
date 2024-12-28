package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.service.UtilisateurService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }
}
