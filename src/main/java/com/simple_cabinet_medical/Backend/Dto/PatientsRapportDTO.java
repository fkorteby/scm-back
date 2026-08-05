package com.simple_cabinet_medical.Backend.Dto;

import java.time.LocalDate;

public class PatientsRapportDTO {

    private Long idPatient;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String numeroTel;
    private String situation;
    private Boolean assurance;
    private String cin;
    private String sexe;
    private String numeroSecuriteSociale;
    private String passport;
    private String acteNaissance;
    private LocalDate lastConsultationDate;

    public PatientsRapportDTO() {
    }

    public PatientsRapportDTO(Long idPatient, String nom, String prenom,
                              LocalDate dateNaissance, String numeroTel,
                              String situation, Boolean assurance,
                              String cin, String sexe,
                              String numeroSecuriteSociale,
                              String passport, String acteNaissance,
                              LocalDate lastConsultationDate) {
        this.idPatient = idPatient;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.numeroTel = numeroTel;
        this.situation = situation;
        this.assurance = assurance;
        this.cin = cin;
        this.sexe = sexe;
        this.numeroSecuriteSociale = numeroSecuriteSociale;
        this.passport = passport;
        this.acteNaissance = acteNaissance;
        this.lastConsultationDate = lastConsultationDate;
    }

    // getters & setters

    public Long getIdPatient() { return idPatient; }
    public void setIdPatient(Long idPatient) { this.idPatient = idPatient; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getNumeroTel() { return numeroTel; }
    public void setNumeroTel(String numeroTel) { this.numeroTel = numeroTel; }

    public String getSituation() { return situation; }
    public void setSituation(String situation) { this.situation = situation; }

    public Boolean getAssurance() { return assurance; }
    public void setAssurance(Boolean assurance) { this.assurance = assurance; }

    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }

    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }

    public String getNumeroSecuriteSociale() { return numeroSecuriteSociale; }
    public void setNumeroSecuriteSociale(String numeroSecuriteSociale) {
        this.numeroSecuriteSociale = numeroSecuriteSociale;
    }

    public String getPassport() { return passport; }
    public void setPassport(String passport) { this.passport = passport; }

    public String getActeNaissance() { return acteNaissance; }
    public void setActeNaissance(String acteNaissance) {
        this.acteNaissance = acteNaissance;
    }

    public LocalDate getLastConsultationDate() { return lastConsultationDate; }
    public void setLastConsultationDate(LocalDate lastConsultationDate) {
        this.lastConsultationDate = lastConsultationDate;
    }
}
