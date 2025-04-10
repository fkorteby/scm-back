package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class Medicament extends BasedObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMedicament;

    private String nomCommerciale;

    private String dci;

    private String dosage;

    private String conditionnement;

    private String forme;


    public Medicament(String nomCommerciale, String dci, String dosage, String conditionnement, String forme, Long idUtilisateur) {
        this.nomCommerciale = nomCommerciale;
        this.dci = dci;
        this.dosage = dosage;
        this.conditionnement = conditionnement;
        this.forme = forme;
        this.idUtilisateur = idUtilisateur;
    }

    public Medicament() {
    }

    public Long getIdMedicament() {
        return idMedicament;
    }

    public void setIdMedicament(Long idMedicament) {
        this.idMedicament = idMedicament;
    }

    public String getNomCommerciale() {
        return nomCommerciale;
    }

    public void setNomCommerciale(String nomCommerciale) {
        this.nomCommerciale = nomCommerciale;
    }

    public String getDci() {
        return dci;
    }

    public void setDci(String dci) {
        this.dci = dci;
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

    public String getForme() {
        return forme;
    }

    public void setForme(String forme) {
        this.forme = forme;
    }

}
