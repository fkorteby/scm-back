package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.Dto.UtilisateurDto;
import com.simple_cabinet_medical.Backend.model.EROLE;
import com.simple_cabinet_medical.Backend.model.Utilisateur;
import com.simple_cabinet_medical.Backend.payload.request.LoginRequest;
import com.simple_cabinet_medical.Backend.payload.request.RegisterUserRequest;
import com.simple_cabinet_medical.Backend.payload.response.LoginResponse;
import com.simple_cabinet_medical.Backend.service.AuthenticationService;
import com.simple_cabinet_medical.Backend.service.JwtService;
import com.simple_cabinet_medical.Backend.utils.RecaptchaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/api/v1/auth")
@RestController
public class AuthenticationController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final RecaptchaService recaptchaService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService, RecaptchaService recaptchaService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.recaptchaService = recaptchaService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UtilisateurDto> register(@RequestBody RegisterUserRequest request) {
        UtilisateurDto registeredUser = authenticationService.signup(request);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginRequest request) {
        try {
            boolean isRecaptchaValid = recaptchaService.validateToken(request.getRecaptchaToken(), "login");

            if (!isRecaptchaValid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Validation failed"));
            }
            Utilisateur authenticatedUser = authenticationService.authenticate(request);
            String jwtToken = jwtService.generateToken(authenticatedUser);

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setNomUtilisateur(authenticatedUser.getNomUtilisateur());
            loginResponse.setId(authenticatedUser.getIdUtilisateur());
            loginResponse.setToken(jwtToken);
            loginResponse.setRole(authenticatedUser.getRole().toString());

            if (authenticatedUser.getRole() == EROLE.ADMIN){
                loginResponse.setClient(null);
            }
            else if (authenticatedUser.getRole() != EROLE.ADMIN) {
                loginResponse.setClient(authenticatedUser.getClient().getIdClient());
            }
            return ResponseEntity.ok(loginResponse);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Authentication failed"));
        }
    }

    @GetMapping("/currentUser")
    public ResponseEntity<UtilisateurDto> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Utilisateur currentUser = (Utilisateur) authentication.getPrincipal();
        UtilisateurDto userDto = new UtilisateurDto();
        userDto.setIdUtilisateur(currentUser.getIdUtilisateur());
        userDto.setNom(currentUser.getNom());
        userDto.setPrenom(currentUser.getPrenom());
        userDto.setNomUtilisateur(currentUser.getNomUtilisateur());
        userDto.setRole(currentUser.getRole());
        if (currentUser.getClient() != null) {
            userDto.setClientNom(currentUser.getClient().getNomClient());
        } else {
            userDto.setClientNom("No client associated");
        }
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("change-password")
    public ResponseEntity<Void> changePassword(@RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");
        String oldPassword = request.get("oldPassword");
        authenticationService.changePassword(oldPassword, newPassword);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/user/{id}")
    public ResponseEntity<UtilisateurDto> updateUser(@PathVariable Long id, @RequestBody RegisterUserRequest request) {
        UtilisateurDto updatedUser = authenticationService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }
}

