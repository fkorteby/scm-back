package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.model.Utilisateur;
import com.simple_cabinet_medical.Backend.payload.request.LoginRequest;
import com.simple_cabinet_medical.Backend.payload.request.RegisterUserRequest;
import com.simple_cabinet_medical.Backend.payload.response.LoginResponse;
import com.simple_cabinet_medical.Backend.service.AuthenticationService;
import com.simple_cabinet_medical.Backend.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/auth")
@RestController
public class AuthenticationController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<Utilisateur> register(@RequestBody RegisterUserRequest request) {
        Utilisateur registeredUser = authenticationService.signup(request);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginRequest request) {
        Utilisateur authenticatedUser = authenticationService.authenticate(request);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setNomUtilisateur(authenticatedUser.getNomUtilisateur());
        loginResponse.setId(authenticatedUser.getIdUtilisateur());
        loginResponse.setToken(jwtToken);
        loginResponse.setRole(authenticatedUser.getRole().toString());
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/me")
    public Authentication getUserInfo() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}

