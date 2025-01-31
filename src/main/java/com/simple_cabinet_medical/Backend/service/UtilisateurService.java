package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.model.Utilisateur;
import com.simple_cabinet_medical.Backend.payload.request.AddUtilisateurRequest;
import com.simple_cabinet_medical.Backend.repository.UtilisateurRepository;
import com.simple_cabinet_medical.Backend.utils.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    private final PasswordEncoder passwordEncoder;

    public UtilisateurService (UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<?> addUtilisateur (AddUtilisateurRequest data) {
        return null;
    }

    public ResponseEntity<?> deleteUtilisateur (Long idUtilisateur) {
        if (utilisateurRepository.findById(idUtilisateur).isPresent()) {
            utilisateurRepository.deleteById(idUtilisateur);
            return ResponseHandler.generateResponse("Utilisateur Bien supprimé", HttpStatus.OK, null);
        } else {
            return ResponseHandler.generateResponse("Utilisateur n'existe pas", HttpStatus.NOT_FOUND, null);
        }
    }

    public ResponseEntity<?> updateUtilisateur (Long idUtilisateur, AddUtilisateurRequest data) {
        if (utilisateurRepository.findById(idUtilisateur).isPresent()) {
            Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur).get();
            utilisateur.setNom(data.getNom());

            utilisateur.setMdp(passwordEncoder.encode(utilisateur.getPassword()));
            utilisateur = utilisateurRepository.save(utilisateur);
            return ResponseHandler.generateResponse("Utilisateur Bien Enregistré", HttpStatus.CREATED, utilisateur);
        } else {
            return ResponseHandler.generateResponse("Utilisateur n'existe pas", HttpStatus.NOT_FOUND, null);
        }
    }
}
