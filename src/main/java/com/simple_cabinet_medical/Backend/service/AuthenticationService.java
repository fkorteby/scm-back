package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.model.EROLE;
import com.simple_cabinet_medical.Backend.model.Utilisateur;
import com.simple_cabinet_medical.Backend.payload.request.LoginRequest;
import com.simple_cabinet_medical.Backend.payload.request.RegisterUserRequest;
import com.simple_cabinet_medical.Backend.repository.ClientRepository;
import com.simple_cabinet_medical.Backend.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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


    public Utilisateur signup(RegisterUserRequest input) {

        Utilisateur user = new Utilisateur();

        user.setNom(input.getNom());
        user.setNomUtilisateur(input.getNomUtilisateur());
        user.setMdp(passwordEncoder.encode(input.getMdp()));
        user.setRole(EROLE.valueOf(input.getRole()));
        user.setClient(clientRepository.findById(input.getIdClient()).orElseThrow());

        return utilisateurRepository.save(user);
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
}
