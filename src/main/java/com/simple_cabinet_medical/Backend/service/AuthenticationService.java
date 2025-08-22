package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.Dto.UtilisateurDto;
import com.simple_cabinet_medical.Backend.Mapper.Utilisateur.UtilisateurMapper;
import com.simple_cabinet_medical.Backend.model.EROLE;
import com.simple_cabinet_medical.Backend.model.Utilisateur;
import com.simple_cabinet_medical.Backend.payload.request.LoginRequest;
import com.simple_cabinet_medical.Backend.payload.request.RegisterUserRequest;
import com.simple_cabinet_medical.Backend.repository.ClientRepository;
import com.simple_cabinet_medical.Backend.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private final UtilisateurMapper utilisateurMapper;

    public AuthenticationService(UtilisateurMapper utilisateurMapper) {
        this.utilisateurMapper = utilisateurMapper;
    }

    public UtilisateurDto signup(RegisterUserRequest input) {

        Utilisateur user = new Utilisateur();

        user.setNom(input.getNom());
        user.setNomUtilisateur(input.getNomUtilisateur());
        user.setMdp(passwordEncoder.encode(input.getMdp()));
        user.setRole(EROLE.valueOf(input.getRole()));
        user.setPrenom(input.getPrenom());
        if (!input.getRole().equals(EROLE.ADMIN)) {
              user.setClient(clientRepository.findById(input.getIdClient()).orElseThrow());
        }
        if (input.getRole().equals(EROLE.ADMIN)) {
            user.setClient(null);
        }
        Utilisateur utilisateur = utilisateurRepository.save(user);
        return utilisateurMapper.EntityToDto(utilisateur);
    }

    public Utilisateur authenticate(LoginRequest input) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getNomUtilisateur(),
                        input.getMdp()
                )
        );

        return utilisateurRepository.findByNomUtilisateur(input.getNomUtilisateur())
                .orElseThrow();
    }

    public void changePassword(String oldPassword, String newPassword) {
        if (oldPassword == null || newPassword == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Utilisateur currentUser = (Utilisateur) authentication.getPrincipal();
        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        currentUser.setMdp(passwordEncoder.encode(newPassword));
        currentUser.setClient(currentUser.getClient());
        currentUser.setPrenom(currentUser.getPrenom());
        currentUser.setNom(currentUser.getNom());
        currentUser.setNomUtilisateur(currentUser.getNomUtilisateur());
        currentUser.setRole(currentUser.getRole());
    }

    public UtilisateurDto updateUser(Long id, RegisterUserRequest request) {
    Utilisateur utilisateur = utilisateurRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    utilisateur.setNom(request.getNom());
    utilisateur.setPrenom(request.getPrenom());
    utilisateur.setClient(utilisateur.getClient());
    utilisateur.setNomUtilisateur(request.getNomUtilisateur());
    utilisateur.setMdp(utilisateur.getMdp());
        return null;
    }
}
