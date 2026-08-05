package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.Dto.UtilisateurDto;
import com.simple_cabinet_medical.Backend.Mapper.Utilisateur.UtilisateurMapper;
import com.simple_cabinet_medical.Backend.exception.InvalidCredentialsException;
import com.simple_cabinet_medical.Backend.model.*;
import com.simple_cabinet_medical.Backend.payload.request.LoginRequest;
import com.simple_cabinet_medical.Backend.payload.request.RegisterUserRequest;
import com.simple_cabinet_medical.Backend.payload.response.LoginResponse;
import com.simple_cabinet_medical.Backend.repository.AnnonceurRepository;
import com.simple_cabinet_medical.Backend.repository.ClientRepository;
import com.simple_cabinet_medical.Backend.repository.TokenRepository;
import com.simple_cabinet_medical.Backend.repository.UtilisateurRepository;
import com.simple_cabinet_medical.Backend.utils.RecaptchaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;


@Service
public class AuthenticationService {

    private static final Logger log =
            LoggerFactory.getLogger(AuthenticationService.class);
    private final UtilisateurRepository utilisateurRepository;

    private final ClientRepository clientRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;
    private final UtilisateurMapper utilisateurMapper;

    private final RecaptchaService recaptchaService;
    private final ClientService clientService;
    private final EmailService emailService;

    private final AnnonceurRepository annonceurRepository;

    private final UtilisateurService utilisateurService;
    private final TokenRepository tokenRepository;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    public AuthenticationService(UtilisateurRepository utilisateurRepository, ClientRepository clientRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService, UtilisateurMapper utilisateurMapper, RecaptchaService recaptchaService, ClientService clientService, EmailService emailService, AnnonceurRepository annonceurRepository, UtilisateurService utilisateurService, TokenRepository tokenRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.utilisateurMapper = utilisateurMapper;
        this.recaptchaService = recaptchaService;
        this.clientService = clientService;
        this.emailService = emailService;
        this.annonceurRepository = annonceurRepository;
        this.utilisateurService = utilisateurService;
        this.tokenRepository = tokenRepository;
    }

    public UtilisateurDto signup(RegisterUserRequest input) {

        Utilisateur user = new Utilisateur();

        user.setNom(input.getNom());
        user.setPrenom(input.getPrenom());
        user.setEmail(input.getEmail());
        user.setNomUtilisateur(utilisateurService.generateUsername(input.getNom(), input.getPrenom()));
        String password = utilisateurService.generateRandomPassword(10);
        user.setMdp(passwordEncoder.encode(password));
        user.setRole(input.getRole());
        if (!input.getRole().equals(EROLE.ADMIN)) {
            user.setClient(clientRepository.findById(input.getIdClient()).orElseThrow());
        }
        if (input.getRole().equals(EROLE.ADMIN)) {
            user.setClient(null);
        }
        Utilisateur utilisateur = utilisateurRepository.save(user);
        this.emailService.sendUserAccessGrantedEmail(utilisateur.getEmail(), utilisateur, password);
        return utilisateurMapper.EntityToDto(utilisateur);
    }

    public Utilisateur authenticate(LoginRequest input) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getNomUtilisateur(),
                            input.getMdp()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Nom d'utilisateur ou mot de passe incorrect.");
        }

        return utilisateurRepository.findByNomUtilisateur(input.getNomUtilisateur())
                .orElseThrow(() -> new InvalidCredentialsException("Utilisateur introuvable."));
    }

    public LoginResponse login(LoginRequest request) {
        Utilisateur authenticatedUser = authenticate(request);

        if (authenticatedUser.getStatus() == EStatus.INACTIVE) {
            log.warn("Login refused: user inactive id={}", authenticatedUser.getIdUtilisateur());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Votre compte est inactif");
        }

        // ===== CLIENT CHECK =====
        if (!authenticatedUser.getRole().equals(EROLE.ANNONCEUR)) {

            if (authenticatedUser.getClient() == null) {
                log.error("Client is NULL for user id={}", authenticatedUser.getIdUtilisateur());
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Client introuvable");
            }

            Long clientId = authenticatedUser.getClient().getIdClient();
            log.info("Checking client status clientId={}", clientId);

            if (!clientService.checkClientStatus(clientId)) {
                log.warn("Client disabled clientId={}", clientId);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Client désactivé");
            }
        }

        // ===== ANNONCEUR =====
        Annonceur annonceur = null;

        if (authenticatedUser.getRole().equals(EROLE.ANNONCEUR)) {

            log.info("Loading annonceur for user id={}", authenticatedUser.getIdUtilisateur());

            annonceur = annonceurRepository
                    .findByUtilisateur_IdUtilisateur(authenticatedUser.getIdUtilisateur())
                    .orElseThrow(() -> {
                        log.error("Annonceur not found for user id={}", authenticatedUser.getIdUtilisateur());
                        return new ResponseStatusException(HttpStatus.FORBIDDEN, "Annonceur introuvable");
                    });

            log.info("Annonceur loaded idAnnonceur={}", annonceur.getIdAnnonceur());
        }

        // ===== JWT =====
        String jwtToken = jwtService.generateToken(authenticatedUser);
        log.info("JWT generated");

        LoginResponse response = new LoginResponse();
        response.setNomUtilisateur(authenticatedUser.getNomUtilisateur());
        response.setId(authenticatedUser.getIdUtilisateur());
        response.setToken(jwtToken);
        response.setRole(authenticatedUser.getRole().toString());

        if (annonceur != null && annonceur.getIdAnnonceur() != null) {
            response.setAnnonceur(annonceur.getIdAnnonceur());
        }

        if (!authenticatedUser.getRole().equals(EROLE.ANNONCEUR)
                && authenticatedUser.getClient() != null) {

            response.setClient(authenticatedUser.getClient().getIdClient());
        }

        // ===== TOKEN SAVE =====
        if (!authenticatedUser.getRole().equals(EROLE.ANNONCEUR)
                && !authenticatedUser.getRole().equals(EROLE.ADMIN)) {

            Instant expiry = Instant.now().plusMillis(jwtExpiration);

            Long clientId = authenticatedUser.getClient() != null
                    ? authenticatedUser.getClient().getIdClient()
                    : null;

            log.info("Saving token userId={} clientId={}",
                    authenticatedUser.getIdUtilisateur(), clientId);

            Token token = new Token(
                    jwtToken,
                    expiry,
                    authenticatedUser.getIdUtilisateur(),
                    clientId
            );
            tokenRepository.save(token);
        }
        return response;
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
        utilisateurRepository.save(currentUser);
    }

    public UtilisateurDto getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Utilisateur non authentifié"
            );
        }

        Utilisateur currentUser = (Utilisateur) authentication.getPrincipal();

        UtilisateurDto userDto = new UtilisateurDto();
        userDto.setIdUtilisateur(currentUser.getIdUtilisateur());
        userDto.setNom(currentUser.getNom());
        userDto.setPrenom(currentUser.getPrenom());
        userDto.setNomUtilisateur(currentUser.getNomUtilisateur());
        userDto.setRole(currentUser.getRole());
        userDto.setEmail(currentUser.getEmail());

        if (currentUser.getClient() != null) {
            userDto.setClientNom(currentUser.getClient().getNomClient());
        } else {
            userDto.setClientNom("No client associated");
        }

        return userDto;
    }

    public void resertPassword(String userName) {
        Utilisateur utilisateur = utilisateurRepository.findByNomUtilisateur(userName)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Utilisateur non trouvé"
                ));
        String newPassword = utilisateurService.generateRandomPassword(10);
        utilisateur.setMdp(passwordEncoder.encode(newPassword));
        utilisateurRepository.save(utilisateur);
        this.emailService.sendPasswordResetEmail(utilisateur.getEmail(), utilisateur, newPassword);
    }
}
