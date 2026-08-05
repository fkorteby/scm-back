package com.simple_cabinet_medical.Backend.payload.request;


import com.simple_cabinet_medical.Backend.model.EROLE;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RegisterUserRequest {

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    private String email;

    @NotBlank(message = "Le Nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le Prenom est obligatoire")
    private String prenom;

    @NotBlank(message = "Le Role est obligatoire")
    private EROLE role;
    @NotBlank(message = "Le client est obligatoire")
    private Long idClient;

    public RegisterUserRequest() {
    }

    public RegisterUserRequest(String email, String nom, String prenom, EROLE role, Long idClient) {
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.idClient = idClient;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public EROLE getRole() {
        return role;
    }

    public void setRole(EROLE role) {
        this.role = role;
    }

    public Long getIdClient() {
        return idClient;
    }

    public void setIdClient(Long idClient) {
        this.idClient = idClient;
    }
}