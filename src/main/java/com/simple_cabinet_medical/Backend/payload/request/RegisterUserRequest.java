package com.simple_cabinet_medical.Backend.payload.request;


public class RegisterUserRequest {

    private String nomUtilisateur;
    private String mdp;
    private String nom;
    private String prenom;
    private String role;
    private Long idClient;

    public RegisterUserRequest(String nomUtilisateur, String mdp, String nom, String prenom, String role, Long idClient) {
        this.nomUtilisateur = nomUtilisateur;
        this.mdp = mdp;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.idClient = idClient;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getIdClient() {
        return idClient;
    }

    public void setIdClient(Long idClient) {
        this.idClient = idClient;
    }
}
