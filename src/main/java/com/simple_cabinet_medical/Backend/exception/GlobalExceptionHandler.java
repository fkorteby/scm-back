package com.simple_cabinet_medical.Backend.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Exceptions métier volontaires (ex: "Email déjà utilisé", "Téléphone déjà utilisé"
    // levées manuellement dans ClientService.clientRegister) -> 400 avec le vrai message.
    // Sans ce handler, elles tombaient dans handleRuntimeException plus bas (statut 500
    // au lieu de 400, alors que ce sont des erreurs de saisie utilisateur, pas des bugs).
    @ExceptionHandler(CustomBadRequestException.class)
    public ResponseEntity<Map<String, String>> handleCustomBadRequest(CustomBadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(RecaptchaException.class)
    public ResponseEntity<Map<String, String>> handleRecaptchaError(RecaptchaException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Contrainte d’unicité");

        String cause = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : "";
        String message = "Une valeur existe déjà, elle doit être unique.";

        Map<String, String> fieldMessages = Map.ofEntries(
                Map.entry("nom_client", "Le nom du client existe déjà."),
                Map.entry("nom_client_en_arabe", "Le nom du client en arabe existe déjà."),
                Map.entry("email", "L’adresse e-mail existe déjà."),
                Map.entry("contact", "Le contact existe déjà."),
                Map.entry("adresse", "L’adresse existe déjà."),
                Map.entry("telephone", "Le numéro de téléphone existe déjà."),
                Map.entry("mobile", "Le numéro de mobile existe déjà."),
                Map.entry("site", "Le site web existe déjà."),
                Map.entry("nom_utilisateur", "Le nom d’utilisateur existe déjà."),
                Map.entry("valeur_document", "La valeur du document existe déjà.")
        );
        for (Map.Entry<String, String> entry : fieldMessages.entrySet()) {
            if (cause.contains(entry.getKey())) {
                message = entry.getValue();
                break;
            }
        }

        error.put("message", message);
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "L’élément demandé est introuvable."));
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Map.of("message", "Action non autorisée sur cette ressource."));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleInvalidJson(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "Format de données invalide."));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "Accès refusé. Vous n’avez pas les permissions nécessaires."));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "Certaines contraintes ne sont pas respectées."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Une erreur interne s’est produite. Veuillez réessayer plus tard."));
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Unexpected error occurred");
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<Map<String, String>> handleJwtSignatureException(SignatureException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Token invalide ou signature incorrecte"));
    }
}