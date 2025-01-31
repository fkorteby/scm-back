package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Duree extends BasedObject{

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long idDuree;

    private String duree;


    public Duree(Long idDuree, String duree, Long idUtilisateur) {
        this.idDuree = idDuree;
        this.duree = duree;
        this.idUtilisateur = idUtilisateur;
    }

    public Duree( String duree, Long idUtilisateur) {
        this.duree = duree;
        this.idUtilisateur = idUtilisateur;
    }

    public Duree() {}


    public Long getIdDuree() {
        return idDuree;
    }

    public void setIdDuree(Long idDuree) {
        this.idDuree = idDuree;
    }

    public String getDuree() {
        return duree;
    }

    public void setDuree(String duree) {
        this.duree = duree;
    }

}
