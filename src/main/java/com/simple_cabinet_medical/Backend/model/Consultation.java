package com.simple_cabinet_medical.Backend.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.Date;


@Entity
public class Consultation extends BasedObject{

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long idConsultation;

    private String motifConsultation;

    private Date dateConsultation;

    private String resultatExamenClinique;

    private String resultatExamenParacliniques;

    private String diagnosticMedical;

    @OneToOne (fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    private Ordonnance ordonnance;

    @ManyToOne
    private Patient patient;

    public Consultation() {}

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Long getIdConsultation() {
        return idConsultation;
    }

    public void setIdConsultation(Long idConsultation) {
        this.idConsultation = idConsultation;
    }

    public String getMotifConsultation() {
        return motifConsultation;
    }

    public void setMotifConsultation(String motifConsultation) {
        this.motifConsultation = motifConsultation;
    }

    public Date getDateConsultation() {
        return dateConsultation;
    }

    public void setDateConsultation(Date dateConsultation) {
        this.dateConsultation = dateConsultation;
    }

    public String getResultatExamenClinique() {
        return resultatExamenClinique;
    }

    public void setResultatExamenClinique(String resultatExamenClinique) {
        this.resultatExamenClinique = resultatExamenClinique;
    }

    public String getResultatExamenParacliniques() {
        return resultatExamenParacliniques;
    }

    public void setResultatExamenParacliniques(String resultatExamenParacliniques) {
        this.resultatExamenParacliniques = resultatExamenParacliniques;
    }

    public String getDiagnosticMedical() {
        return diagnosticMedical;
    }

    public void setDiagnosticMedical(String diagnosticMedical) {
        this.diagnosticMedical = diagnosticMedical;
    }

    public Ordonnance getOrdonnance() {
        return ordonnance;
    }

    public void setOrdonnance(Ordonnance ordonnance) {
        this.ordonnance = ordonnance;
    }

}
