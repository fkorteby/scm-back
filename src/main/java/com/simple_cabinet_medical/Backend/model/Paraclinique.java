package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
public class Paraclinique {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long idParaclinique;

    private String examen;

    private String type;

    private Long idUtilisateur;


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

    public Paraclinique() {}


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

    public Long getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Long idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }
}
