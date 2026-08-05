package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Date;


@Entity
public class Forme extends BasedObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idForme;

    @Column(nullable = false)
    private String forme;

    @Column(nullable = false)
    private String abreviation;


    public Forme(Long idForme, String forme, String abreviation) {
        this.idForme = idForme;
        this.forme = forme;
        this.abreviation = abreviation;
    }

    public Forme() {

    }

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
