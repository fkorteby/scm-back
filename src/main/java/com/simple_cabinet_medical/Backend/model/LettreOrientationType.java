package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class LettreOrientationType extends BasedObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLettreOrientation;

    private String nomLettreOrientation;

    private String text;

    public LettreOrientationType(Long idLettreOrientation, String nomLettreOrientation, String text) {
        this.idLettreOrientation = idLettreOrientation;
        this.nomLettreOrientation = nomLettreOrientation;
        this.text = text;
    }

    public LettreOrientationType() {

    }

    public Long getIdLettreOrientation() {
        return idLettreOrientation;
    }

    public void setIdLettreOrientation(Long idLettreOrientation) {
        this.idLettreOrientation = idLettreOrientation;
    }

    public String getNomLettreOrientation() {
        return nomLettreOrientation;
    }

    public void setNomLettreOrientation(String nomLettreOrientation) {
        this.nomLettreOrientation = nomLettreOrientation;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
