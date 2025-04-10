package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Conduite extends BasedObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idConduite;

    private String conduite;


    public Conduite(Long idConduite, String conduite, Long idUtilisateur) {
        this.idConduite = idConduite;
        this.conduite = conduite;
        this.idUtilisateur = idUtilisateur;
    }

    public Conduite(String conduite, Long idUtilisateur) {
        this.conduite = conduite;
        this.idUtilisateur = idUtilisateur;
    }

    public Conduite() {
    }


    public Long getIdConduite() {
        return idConduite;
    }

    public void setIdConduite(Long idConduite) {
        this.idConduite = idConduite;
    }

    public String getConduite() {
        return conduite;
    }

    public void setConduite(String conduite) {
        this.conduite = conduite;
    }

}
