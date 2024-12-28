package com.simple_cabinet_medical.Backend.payload.request;


public class RegisterUserRequest {

    private String nomUtilisateur;
    private String mdp;
    private String nom;
    private String role;

    public RegisterUserRequest() {}

    public RegisterUserRequest(String nomUtilisateur, String mdp, String nom, String role) {
        this.nomUtilisateur = nomUtilisateur;
        this.mdp = mdp;
        this.nom = nom;
        this.role = role;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
