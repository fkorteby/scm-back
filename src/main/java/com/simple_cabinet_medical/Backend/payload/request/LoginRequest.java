package com.simple_cabinet_medical.Backend.payload.request;


public class LoginRequest {

    private String nomUtilisateur;
    private String mdp;

    public LoginRequest() {}

    public LoginRequest(String nomUtilisateur, String mdp) {
        this.nomUtilisateur = nomUtilisateur;
        this.mdp = mdp;
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
}
