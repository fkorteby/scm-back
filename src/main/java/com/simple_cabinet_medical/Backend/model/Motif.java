package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Motif extends BasedObject{

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long idMotif;

    private String motif;


    public Motif() {}


    public Motif(Long idMotif, String motif, Long idUtilisateur) {
        this.idMotif = idMotif;
        this.motif = motif;
        this.idUtilisateur = idUtilisateur;
    }

    public Motif(String motif, Long idUtilisateur) {
        this.motif = motif;
        this.idUtilisateur = idUtilisateur;
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
