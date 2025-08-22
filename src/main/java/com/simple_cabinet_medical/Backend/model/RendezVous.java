package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class RendezVous extends BasedObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRendezVous;

    private LocalDate dateRendezVous;
    private String heureRendezVous;
    private String notes;

    private EStatusRendezVous statusRendezVous;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @OneToOne
    @JoinColumn(name = "consultation_id", referencedColumnName = "idConsultation")
    private Consultation consultation;

    public RendezVous(Long idRendezVous, LocalDate dateRendezVous, String heureRendezVous, String notes, EStatusRendezVous statusRendezVous, Patient patient, Consultation consultation) {
        this.idRendezVous = idRendezVous;
        this.dateRendezVous = dateRendezVous;
        this.heureRendezVous = heureRendezVous;
        this.notes = notes;
        this.statusRendezVous = statusRendezVous;
        this.patient = patient;
        this.consultation = consultation;
    }

    public RendezVous() {

    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public EStatusRendezVous getStatusRendezVous() {
        return statusRendezVous;
    }

    public void setStatusRendezVous(EStatusRendezVous statusRendezVous) {
        this.statusRendezVous = statusRendezVous;
    }

    public Long getIdRendezVous() {
        return idRendezVous;
    }

    public void setIdRendezVous(Long idRendezVous) {
        this.idRendezVous = idRendezVous;
    }

    public LocalDate getDateRendezVous() {
        return dateRendezVous;
    }

    public void setDateRendezVous(LocalDate dateRendezVous) {
        this.dateRendezVous = dateRendezVous;
    }

    public String getHeureRendezVous() {
        return heureRendezVous;
    }

    public void setHeureRendezVous(String heureRendezVous) {
        this.heureRendezVous = heureRendezVous;
    }

    public String getNote() {
        return notes;
    }

    public void setNote(String note) {
        this.notes = note;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Consultation getConsultation() {
        return consultation;
    }

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
    }
}