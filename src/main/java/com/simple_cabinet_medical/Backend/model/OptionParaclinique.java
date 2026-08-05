package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;

@Entity
public class OptionParaclinique extends BasedObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOptionParaclinique;

    @Column(nullable = false)
    private String option;


    public OptionParaclinique(Long idOptionParaclinique, String option) {
        this.idOptionParaclinique = idOptionParaclinique;
        this.option = option;
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
