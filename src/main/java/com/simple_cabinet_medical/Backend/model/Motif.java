package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity

public class Motif {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long idMotif;

    private String motif;

    private Long idUtilisateur;


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

    public Long getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Long idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }
}
