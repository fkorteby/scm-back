package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;


@Entity
public class Document extends BasedObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDocument;

    private String nomDocument;

    private String text;

    private ETypeDocument ETypeDocument;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "idPatient")
    private Patient patient;

    public Document() {
    }

    public Document(Long idDocument, String nomDocument, String text, ETypeDocument ETypeDocument, Patient patient) {
        this.idDocument = idDocument;
        this.nomDocument = nomDocument;
        this.text = text;
        this.ETypeDocument = ETypeDocument;
        this.patient = patient;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ETypeDocument getTypeDocument() {
        return ETypeDocument;
    }

    public void setTypeDocument(ETypeDocument ETypeDocument) {
        this.ETypeDocument = ETypeDocument;
    }
}
