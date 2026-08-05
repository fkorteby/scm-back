package com.simple_cabinet_medical.Backend.Dto.Dash;

import java.time.LocalDate;

public class PatientAujhDto {
    private Long id;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String motifConsultation;
    private LocalDate dateConsultation;

    public PatientAujhDto(Long id, String nom, String prenom, LocalDate dateNaissance, String motifConsultation, LocalDate dateConsultation) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.motifConsultation = motifConsultation;
        this.dateConsultation = dateConsultation;
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

    public String getMotifConsultation() {
        return motifConsultation;
    }

    public void setMotifConsultation(String motifConsultation) {
        this.motifConsultation = motifConsultation;
    }

    public LocalDate getDateConsultation() {
        return dateConsultation;
    }

    public void setDateConsultation(LocalDate dateConsultation) {
        this.dateConsultation = dateConsultation;
    }
}
