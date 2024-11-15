package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.model.Utilisateur;
import com.simple_cabinet_medical.Backend.payload.request.LoginRequest;
import com.simple_cabinet_medical.Backend.payload.request.RegisterUserRequest;
import com.simple_cabinet_medical.Backend.payload.response.LoginResponse;
import com.simple_cabinet_medical.Backend.repository.UtilisateurRepository;
import com.simple_cabinet_medical.Backend.service.AuthenticationService;
import com.simple_cabinet_medical.Backend.service.JwtService;
import com.simple_cabinet_medical.Backend.service.UserDetailsServiceImpl;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthenticationController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping("/signup")
    public ResponseEntity<Utilisateur> register(@RequestBody RegisterUserRequest request) {
        Utilisateur registeredUser = authenticationService.signup(request);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginRequest request) {
        
        Utilisateur utilisateur = utilisateurRepository.findByNomUtilisateur(request.getNomUtilisateur()).get();

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getNomUtilisateur());

        String jwtToken = jwtService.generateToken(userDetails);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setNomUtilisateur(utilisateur.getNomUtilisateur());
        loginResponse.setId(utilisateur.getIdUtilisateur());
        loginResponse.setToken(jwtToken);
        loginResponse.setRole(utilisateur.getRole());
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> getUsers () {
        return ResponseEntity.ok("Hello world everyone");
    }
}
