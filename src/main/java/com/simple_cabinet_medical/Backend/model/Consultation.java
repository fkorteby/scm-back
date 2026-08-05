package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;


@Entity
public class Consultation extends BasedObject implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idConsultation;

    private LocalDate dateConsultation;

    private EStatusConsultation statusConsultation;

    @Column(columnDefinition = "TEXT")
    private String resultatExamenClinique;

    @Column(columnDefinition = "TEXT")
    private String resultatExamenParacliniques;

    @Column(columnDefinition = "TEXT")
    private String diagnosticMedical;

    @Column(columnDefinition = "TEXT")
    private String traitement;

    @Column(columnDefinition = "TEXT")
    private String catEvolution;

    @Column(columnDefinition = "TEXT")
    private String motifConsultation;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "idPatient")
    private Patient patient;

    @OneToOne(mappedBy = "consultation")
    private RendezVous rendezVous;

    @OneToMany(mappedBy = "consultation",cascade = CascadeType.ALL)
    private List<Traitement> traitements;


    public Consultation() {

    }

    public Consultation(Long idConsultation, LocalDate dateConsultation, EStatusConsultation statusConsultation, String resultatExamenClinique, String resultatExamenParacliniques, String diagnosticMedical, String traitement, String catEvolution, String motifCnsultation, Client client, Patient patient, RendezVous rendezVous, List<Traitement> traitements) {
        this.idConsultation = idConsultation;
        this.dateConsultation = dateConsultation;
        this.statusConsultation = statusConsultation;
        this.resultatExamenClinique = resultatExamenClinique;
        this.resultatExamenParacliniques = resultatExamenParacliniques;
        this.diagnosticMedical = diagnosticMedical;
        this.traitement = traitement;
        this.catEvolution = catEvolution;
        this.motifConsultation = motifCnsultation;
        this.client = client;
        this.patient = patient;
        this.rendezVous = rendezVous;
        this.traitements = traitements;
    }

    public Long getIdConsultation() {
        return idConsultation;
    }

    public void setIdConsultation(Long idConsultation) {
        this.idConsultation = idConsultation;
    }

    public LocalDate getDateConsultation() {
        return dateConsultation;
    }

    public void setDateConsultation(LocalDate dateConsultation) {
        this.dateConsultation = dateConsultation;
    }

    public EStatusConsultation getStatusConsultation() {
        return statusConsultation;
    }

    public void setStatusConsultation(EStatusConsultation statusConsultation) {
        this.statusConsultation = statusConsultation;
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

    public String getTraitement() {
        return traitement;
    }

    public void setTraitement(String traitement) {
        this.traitement = traitement;
    }

    public String getCatEvolution() {
        return catEvolution;
    }

    public void setCatEvolution(String catEvolution) {
        this.catEvolution = catEvolution;
    }

    public String getMotifConsultation() {
        return motifConsultation;
    }

    public void setMotifConsultation(String motifCnsultation) {
        this.motifConsultation = motifCnsultation;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public RendezVous getRendezVous() {
        return rendezVous;
    }

    public void setRendezVous(RendezVous rendezVous) {
        this.rendezVous = rendezVous;
    }

    public List<Traitement> getTraitements() {
        return traitements;
    }

    public void setTraitements(List<Traitement> traitements) {
        this.traitements = traitements;
    }
}
