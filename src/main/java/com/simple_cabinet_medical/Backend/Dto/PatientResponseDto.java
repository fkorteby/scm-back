package com.simple_cabinet_medical.Backend.Dto;

import com.simple_cabinet_medical.Backend.Projection.ConsultationInfo;

import java.util.Date;

public class PatientResponseDto {
    private Long idPatient;
    private String nom;
    private String prenom;
    private String telephone;
    private String situation;
    private Date dateNaissance;
    private ConsultationInfo consultationInfo;
    // Constructeur par défaut
    public PatientResponseDto(Long idPatient, String nom, String prenom, String numeroTel) {}

    public ConsultationInfo getConsultationInfo() {
        return consultationInfo;
    }

    public void setConsultationInfo(ConsultationInfo consultationInfo) {
        this.consultationInfo = consultationInfo;
    }

    public PatientResponseDto(Long idPatient, String nom, String prenom, String telephone, ConsultationInfo consultationInfo) {
        this.idPatient = idPatient;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.consultationInfo = consultationInfo;
    }

    // Getters et setters
    public Long getIdPatient() { return idPatient; }
    public void setIdPatient(Long idPatient) { this.idPatient = idPatient; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
}