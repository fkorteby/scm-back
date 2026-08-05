package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;

@Entity
public class ExamenParacliniqueType extends BasedObject{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idExamenParaclinique;

    @Column(nullable = false)
    private String nomExamenParaclinique;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    public ExamenParacliniqueType(Long idExamenParaclinique, String nomExamenParaclinique, String text) {
        this.idExamenParaclinique = idExamenParaclinique;
        this.nomExamenParaclinique = nomExamenParaclinique;
        this.text = text;
    }

    public ExamenParacliniqueType() {

    }

    public Long getIdExamenParaclinique() {
        return idExamenParaclinique;
    }

    public void setIdExamenParaclinique(Long idExamenParaclinique) {
        this.idExamenParaclinique = idExamenParaclinique;
    }

    public String getNomExamenParaclinique() {
        return nomExamenParaclinique;
    }

    public void setNomExamenParaclinique(String nomExamenParaclinique) {
        this.nomExamenParaclinique = nomExamenParaclinique;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
