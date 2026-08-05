package com.simple_cabinet_medical.Backend.Dto.Dash;

import com.simple_cabinet_medical.Backend.model.EStatusRendezVous;

import java.time.LocalDate;

public class RenderVousAujhDto {
    private Long id;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String heureRendezVous;
    private EStatusRendezVous status;

    public RenderVousAujhDto(Long id, String nom, String prenom, LocalDate dateNaissance, String heureRendezVous, EStatusRendezVous status) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.heureRendezVous = heureRendezVous;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getHeureRendezVous() {
        return heureRendezVous;
    }

    public void setHeureRendezVous(String heureRendezVous) {
        this.heureRendezVous = heureRendezVous;
    }

    public EStatusRendezVous getStatus() {
        return status;
    }

    public void setStatus(EStatusRendezVous status) {
        this.status = status;
    }
}
