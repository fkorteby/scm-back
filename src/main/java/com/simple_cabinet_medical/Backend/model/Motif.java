package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Motif extends BasedObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMotif;

    private String motif;


    public Motif() {
    }

    public Motif(Long idMotif, String motif) {
        this.idMotif = idMotif;
        this.motif = motif;
    }

    public Long getIdMotif() {
        return idMotif;
    }

    public void setIdMotif(Long idMotif) {
        this.idMotif = idMotif;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }
}
