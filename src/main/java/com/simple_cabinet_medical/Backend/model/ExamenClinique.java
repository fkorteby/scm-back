package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;

@Entity
public class ExamenClinique extends BasedObject{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idExamenClinique;
    @Column(nullable = false)
    private String nomExamenClinique;
    @Column(nullable = false)
    private Long idParentExamenClinique;

    public ExamenClinique(Long idExamenClinique, String nomExamenClinique, Long idParentExamenClinique) {
        this.idExamenClinique = idExamenClinique;
        this.nomExamenClinique = nomExamenClinique;
        this.idParentExamenClinique = idParentExamenClinique;
    }

    public ExamenClinique() {

    }

    public Long getIdExamenClinique() {
        return idExamenClinique;
    }

    public void setIdExamenClinique(Long idExamenClinque) {
        this.idExamenClinique = idExamenClinque;
    }

    public String getNomExamenClinique() {
        return nomExamenClinique;
    }

    public void setNomExamenClinique(String nomExamenClinque) {
        this.nomExamenClinique = nomExamenClinque;
    }

    public Long getIdParentExamenClinique() {
        return idParentExamenClinique;
    }

    public void setIdParentExamenClinique(Long idParentExamenClinique) {
        this.idParentExamenClinique = idParentExamenClinique;
    }
}
