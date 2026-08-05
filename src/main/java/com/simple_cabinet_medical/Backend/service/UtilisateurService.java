package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.Dto.UtilisateurDto;
import com.simple_cabinet_medical.Backend.Mapper.Utilisateur.UtilisateurMapper;
import com.simple_cabinet_medical.Backend.model.EStatus;
import com.simple_cabinet_medical.Backend.model.Utilisateur;
import com.simple_cabinet_medical.Backend.payload.request.RegisterUserRequest;
import com.simple_cabinet_medical.Backend.repository.UtilisateurRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurMapper utilisateurMapper;

    public UtilisateurService(UtilisateurRepository utilisateurRepository, UtilisateurMapper utilisateurMapper) {
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurMapper = utilisateurMapper;
    }

    public UtilisateurDto changeStatus(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur not found"));
        utilisateur.setStatus(utilisateur.getStatus().equals(EStatus.INACTIVE) ? EStatus.ACTIVE : EStatus.INACTIVE);
        Utilisateur savedUtilisateur = utilisateurRepository.save(utilisateur);
        return utilisateurMapper.EntityToDto(savedUtilisateur);
    }

    public Page<UtilisateurDto> getAllUtilisateurs(int page, int size) {
        Page<Utilisateur> utilisateurs = utilisateurRepository.findAll(PageRequest.of(page, size));
        return utilisateurs.map(utilisateurMapper::EntityToDto);
    }

    public Page<UtilisateurDto> getAllUtilisateursByClient(Long clientId, int page, int size) {
        Page<Utilisateur> utilisateurs = utilisateurRepository.findByClientIdClient(clientId, PageRequest.of(page, size));
        return utilisateurs.map(utilisateurMapper::EntityToDto);
    }

    public UtilisateurDto getUtilisateur(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur not found"));
        return utilisateurMapper.EntityToDto(utilisateur);
    }
    public void delete(Long id) {
        utilisateurRepository.deleteById(id);
    }
    public UtilisateurDto updateUser(Long id, RegisterUserRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setRole(request.getRole());

        Utilisateur savedUtilisateur = utilisateurRepository.save(utilisateur);
        return utilisateurMapper.EntityToDto(savedUtilisateur);
    }
    public void saveUser(Utilisateur utilisateur) {
        if (utilisateurRepository.existsByNomUtilisateur(utilisateur.getNomUtilisateur())) {
            throw new IllegalArgumentException("Username déjà existant");
        }else
             utilisateurRepository.save(utilisateur);
    }

    public String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%&*!";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public String generateUsername(String nom, String prenom) {
        return (nom + "_" + prenom)
                .toLowerCase();
//                .replaceAll("\\s+", "")
//                .replaceAll("[^a-zA-Z0-9.]", "");
    }
}
