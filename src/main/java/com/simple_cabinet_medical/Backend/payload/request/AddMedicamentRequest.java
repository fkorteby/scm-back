package com.simple_cabinet_medical.Backend.payload.request;

public class AddMedicamentRequest {

    private String nomCommerciale;
    private String dosage;
    private String conditionnement;
    private String dci;
    private String forme;

    private Long idUtilisateur;

    public AddMedicamentRequest() {}

    public AddMedicamentRequest(String nomCommerciale, String dosage, String conditionnement, String dci, String forme, Long idUtilisateur) {
        this.nomCommerciale = nomCommerciale;
        this.dosage = dosage;
        this.conditionnement = conditionnement;
        this.dci = dci;
        this.forme = forme;
        this.idUtilisateur = idUtilisateur;
    }

    public Long getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Long idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getNomCommerciale() {
        return nomCommerciale;
    }

    public void setNomCommerciale(String nomCommerciale) {
        this.nomCommerciale = nomCommerciale;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getConditionnement() {
        return conditionnement;
    }

    public void setConditionnement(String conditionnement) {
        this.conditionnement = conditionnement;
    }

    public String getDci() {
        return dci;
    }

    public void setDci(String dci) {
        this.dci = dci;
    }

    public String getForme() {
        return forme;
    }

    public void setForme(String forme) {
        this.forme = forme;
    }
}
