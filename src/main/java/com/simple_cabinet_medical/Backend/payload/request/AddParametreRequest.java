package com.simple_cabinet_medical.Backend.payload.request;

public class AddParametreRequest {

    private String parametre;
    private Long idUtilisateur;


    public AddParametreRequest() {}

    public AddParametreRequest(String parametre, Long idUtilisateur) {
        this.parametre = parametre;
        this.idUtilisateur = idUtilisateur;
    }

    public String getParametre() {
        return parametre;
    }

    public void setParametre(String parametre) {
        this.parametre = parametre;
    }

    public Long getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Long idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }
}
