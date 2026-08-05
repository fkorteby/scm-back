package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.Dto.UtilisateurDto;
import com.simple_cabinet_medical.Backend.payload.request.LoginRequest;
import com.simple_cabinet_medical.Backend.payload.request.RegisterUserRequest;
import com.simple_cabinet_medical.Backend.payload.response.LoginResponse;
import com.simple_cabinet_medical.Backend.service.AuthenticationService;
import com.simple_cabinet_medical.Backend.utils.RecaptchaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/api/v1/auth")
@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final RecaptchaService recaptchaService;
    public AuthenticationController(AuthenticationService authenticationService, RecaptchaService recaptchaService) {
        this.authenticationService = authenticationService;
        this.recaptchaService = recaptchaService;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN_PRINCIPAL')")
    @PostMapping("/signup")
    public ResponseEntity<UtilisateurDto> register(@RequestBody RegisterUserRequest request) {
        UtilisateurDto registeredUser = authenticationService.signup(request);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(
            @RequestBody LoginRequest request
    ) {
        recaptchaService.validateToken(request.getRecaptchaToken(), "login");
        return ResponseEntity.ok(authenticationService.login(request));
    }
    @PatchMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody Map<String, String> request,@RequestParam String recaptchaToken) {
        recaptchaService.validateToken(recaptchaToken, "reset_password");
        String userName = request.get("userName");
        authenticationService.resertPassword(userName);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/currentUser")
    public ResponseEntity<UtilisateurDto> getCurrentUser() {
        UtilisateurDto utilisateurDto = authenticationService.getCurrentUser();
        return new ResponseEntity<>(utilisateurDto, HttpStatus.OK);
    }

    //    @PreAuthorize("@authz.hasChangePasswordCustomPermission(#id)")
    @PatchMapping("change-password")
    public ResponseEntity<Void> changePassword(@RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");
        String oldPassword = request.get("currentPassword");
        authenticationService.changePassword(oldPassword, newPassword);
        return ResponseEntity.ok().build();
    }
}

