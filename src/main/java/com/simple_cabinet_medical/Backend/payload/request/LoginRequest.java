package com.simple_cabinet_medical.Backend.payload.request;


public class LoginRequest {

    private String nomUtilisateur;
    private String mdp;
    private String recaptchaToken;

    public LoginRequest() {}

    public LoginRequest(String nomUtilisateur, String mdp, String recaptchaToken) {
        this.nomUtilisateur = nomUtilisateur;
        this.mdp = mdp;
        this.recaptchaToken = recaptchaToken;
    }

    public String getRecaptchaToken() {
        return recaptchaToken;
    }

    public void setRecaptchaToken(String recaptchaToken) {
        this.recaptchaToken = recaptchaToken;
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
