package com.simple_cabinet_medical.Backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;


@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Medicament extends BasedObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMedicament;

    @Column(nullable = false)
    private String nomCommerciale;


    private String dci;

    private String dosage;

    private String conditionnement;
    private String laboMedicament;
    private String remMedicament;

    private String forme;

    private String duree;

    private String posologie;

    public Medicament() {
    }

    public Medicament(Long idMedicament, String nomCommerciale, String dci, String dosage, String conditionnement, String laboMedicament, String remMedicament, String forme, String duree, String posologie) {
        this.idMedicament = idMedicament;
        this.nomCommerciale = nomCommerciale;
        this.dci = dci;
        this.dosage = dosage;
        this.conditionnement = conditionnement;
        this.laboMedicament = laboMedicament;
        this.remMedicament = remMedicament;
        this.forme = forme;
        this.duree = duree;
        this.posologie = posologie;
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

    public String getLaboMedicament() {
        return laboMedicament;
    }

    public void setLaboMedicament(String laboMedicament) {
        this.laboMedicament = laboMedicament;
    }

    public String getRemMedicament() {
        return remMedicament;
    }

    public void setRemMedicament(String remMedicament) {
        this.remMedicament = remMedicament;
    }

    public String getForme() {
        return forme;
    }

    public void setForme(String forme) {
        this.forme = forme;
    }

    public String getDuree() {
        return duree;
    }

    public void setDuree(String duree) {
        this.duree = duree;
    }

    public String getPosologie() {
        return posologie;
    }

    public void setPosologie(String posologie) {
        this.posologie = posologie;
    }
}