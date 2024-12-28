package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurService (UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public void addUtilisateur () {}

    public void deleteUtilisateur () {}

    public void updateUtilisateur () {}
}
