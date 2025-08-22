package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class CertificatType extends BasedObject{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCertificat;

    private String nomCertificat;

    private String text;

    public CertificatType(Long idCertificat, String nomCertificat, String text) {
        this.idCertificat = idCertificat;
        this.nomCertificat = nomCertificat;
        this.text = text;
    }

    public CertificatType() {
    }

    public Long getIdCertificat() {
        return idCertificat;
    }

    public void setIdCertificat(Long idCertificat) {
        this.idCertificat = idCertificat;
    }

    public String getNomCertificat() {
        return nomCertificat;
    }

    public void setNomCertificat(String nomCertificat) {
        this.nomCertificat = nomCertificat;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
