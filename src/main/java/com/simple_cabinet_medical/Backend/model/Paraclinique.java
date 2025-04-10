package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Paraclinique extends BasedObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idParaclinique;

    private String examen;

    private String type;


    public Paraclinique(Long idParaclinique, String examen, String type, Long idUtilisateur) {
        this.idParaclinique = idParaclinique;
        this.examen = examen;
        this.type = type;
        this.idUtilisateur = idUtilisateur;
    }

    public Paraclinique(String examen, String type, Long idUtilisateur) {
        this.examen = examen;
        this.type = type;
        this.idUtilisateur = idUtilisateur;
    }

    public Paraclinique() {
    }


    public Long getIdParaclinique() {
        return idParaclinique;
    }

    public void setIdParaclinique(Long idParaclinique) {
        this.idParaclinique = idParaclinique;
    }

    public String getExamen() {
        return examen;
    }

    public void setExamen(String examen) {
        this.examen = examen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
