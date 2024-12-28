package com.simple_cabinet_medical.Backend.payload.request;

public class AddFormeRequest {

    private String forme;
    private String abreviation;

    private Long idUtilisateur;


    public AddFormeRequest() {}

    public AddFormeRequest(String forme, String abreviation, Long idUtilisateur) {
        this.forme = forme;
        this.abreviation = abreviation;
        this.idUtilisateur = idUtilisateur;
    }

    public String getForme() {
        return forme;
    }

    public void setForme(String forme) {
        this.forme = forme;
    }

    public String getAbreviation() {
        return abreviation;
    }

    public void setAbreviation(String abreviation) {
        this.abreviation = abreviation;
    }

    public Long getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Long idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }
}
