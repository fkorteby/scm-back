package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;


@Entity
public class Forme extends BasedObject{

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long idForme;

    @NotNull
    private String forme;

    @NotNull
    private String abreviation;


    public Forme (String forme, String abreviation, Long idUtilisateur) {
        this.forme = forme;
        this.abreviation = abreviation;
        this.idUtilisateur = idUtilisateur;
    }

    public Forme() {}

    public Long getIdForme() {
        return idForme;
    }

    public void setIdForme(Long idForme) {
        this.idForme = idForme;
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

}
