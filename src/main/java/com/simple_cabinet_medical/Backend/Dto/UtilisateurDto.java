package com.simple_cabinet_medical.Backend.Dto;

import com.simple_cabinet_medical.Backend.model.EROLE;
import com.simple_cabinet_medical.Backend.model.EStatus;

public class UtilisateurDto {
    private Long idUtilisateur;
    private String nom;
    private String prenom;
    private String email;
    private String nomUtilisateur;
    private EROLE role;
    private String clientNom;
    private EStatus status;

    public UtilisateurDto(Long idUtilisateur, String nom, String prenom, String email, String nomUtilisateur, EROLE role, String clientNom, EStatus status) {
        this.idUtilisateur = idUtilisateur;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.nomUtilisateur = nomUtilisateur;
        this.role = role;
        this.clientNom = clientNom;
        this.status = status;
    }

    public UtilisateurDto() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Long idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
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

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }

    public EROLE getRole() {
        return role;
    }

    public void setRole(EROLE role) {
        this.role = role;
    }

    public EStatus getStatus() {
        return status;
    }

    public void setStatus(EStatus status) {
        this.status = status;
    }

    public String getClientNom() {
        return clientNom;
    }

    public void setClientNom(String clientNom) {
        this.clientNom = clientNom;
    }
}
