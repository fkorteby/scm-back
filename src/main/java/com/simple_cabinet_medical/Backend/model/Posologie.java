package com.simple_cabinet_medical.Backend.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Posologie {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long idPosologie;

    private String posologie;

    private Long idUtilisateur;

    public Posologie() {}

    public Posologie(Long idPosologie, String posologie, Long idUtilisateur) {
        this.idPosologie = idPosologie;
        this.posologie = posologie;
        this.idUtilisateur = idUtilisateur;
    }

    public Posologie(String posologie, Long idUtilisateur) {
        this.posologie = posologie;
        this.idUtilisateur = idUtilisateur;
    }


    public Long getIdPosologie() {
        return idPosologie;
    }

    public void setIdPosologie(Long idPosologie) {
        this.idPosologie = idPosologie;
    }

    public String getPosologie() {
        return posologie;
    }

    public void setPosologie(String posologie) {
        this.posologie = posologie;
    }

    public Long getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Long idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }
}
