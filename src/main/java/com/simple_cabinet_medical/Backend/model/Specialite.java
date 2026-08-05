package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;

@Table
@Entity
public class Specialite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSpecialite;
    @Column(nullable = false,unique = true)
    private String code;
    @Column(nullable = false,unique = true)
    private String libelle;

    public Specialite() {
    }

    public Specialite(Long idSpecialite, String code, String libelle) {
        this.idSpecialite = idSpecialite;
        this.code = code;
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getIdSpecialite() {
        return idSpecialite;
    }

    public void setIdSpecialite(Long idSpecialite) {
        this.idSpecialite = idSpecialite;
    }
}
