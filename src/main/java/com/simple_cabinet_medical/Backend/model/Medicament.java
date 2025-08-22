package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;


@Entity
public class Medicament extends BasedObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMedicament;

    private String nomCommerciale;

    private String dci;

    private String dosage;

    private String conditionnement;

    @ManyToOne
    @JoinColumn(name = "formeId",referencedColumnName = "idForme")
    private Forme forme;

    public Medicament(Long idMedicament, String nomCommerciale, String dci, String dosage, String conditionnement, Forme forme) {
        this.idMedicament = idMedicament;
        this.nomCommerciale = nomCommerciale;
        this.dci = dci;
        this.dosage = dosage;
        this.conditionnement = conditionnement;
        this.forme = forme;
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

    public Forme getForme() {
        return forme;
    }
    public void setForme(Forme forme) {
        this.forme = forme;
    }
}
