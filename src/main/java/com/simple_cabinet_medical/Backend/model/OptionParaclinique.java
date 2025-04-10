package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class OptionParaclinique extends BasedObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOptionParaclinique;

    private String option;

    private Long idUtilisateur;

    public OptionParaclinique(Long idOptionParaclinique, String option, Long idUtilisateur) {
        this.idOptionParaclinique = idOptionParaclinique;
        this.option = option;
        this.idUtilisateur = idUtilisateur;
    }

    public OptionParaclinique(String option, Long idUtilisateur) {
        this.option = option;
        this.idUtilisateur = idUtilisateur;
    }


    public OptionParaclinique() {
    }


    public Long getIdOptionParaclinique() {
        return idOptionParaclinique;
    }

    public void setIdOptionParaclinique(Long idOptionParaclinique) {
        this.idOptionParaclinique = idOptionParaclinique;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

}
