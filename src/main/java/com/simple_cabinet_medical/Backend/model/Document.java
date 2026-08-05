package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Document extends BasedObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDocument;

    @Column(nullable = false)
    private String nomDocument;

    @Column(nullable = false)
    private LocalDate dateGeneration;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    private ETypeDocument typeDocument;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "idPatient")
    private Patient patient;

    public Document() {
    }

    public Document(Long idDocument, String nomDocument, LocalDate dateGeneration, String text, ETypeDocument typeDocument, Patient patient) {
        this.idDocument = idDocument;
        this.nomDocument = nomDocument;
        this.dateGeneration = dateGeneration;
        this.text = text;
        this.typeDocument = typeDocument;
        this.patient = patient;
    }

    public Long getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(Long idDocument) {
        this.idDocument = idDocument;
    }

    public String getNomDocument() {
        return nomDocument;
    }

    public void setNomDocument(String nomDocument) {
        this.nomDocument = nomDocument;
    }

    public LocalDate getDateGeneration() {
        return dateGeneration;
    }

    public void setDateGeneration(LocalDate dateGeneration) {
        this.dateGeneration = dateGeneration;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ETypeDocument getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(ETypeDocument typeDocument) {
        this.typeDocument = typeDocument;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
