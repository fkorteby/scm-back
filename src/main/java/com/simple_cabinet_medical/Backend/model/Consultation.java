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

    private String resultatExamenClinique;

    private String resultatExamenParacliniques;

    private String diagnosticMedical;

    private String traitement;

    private String motifCnsultation;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "idPatient")
    private Patient patient;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "consultation_id")
    private List<Document> documents;

    @ManyToOne
    @JoinColumn(name = "conduit_id")
    private Conduite conduite;

    @OneToOne(mappedBy = "consultation")
    private RendezVous rendezVous;

    @OneToMany(mappedBy = "consultation")
    private List<Traitement> traitements;

    public RendezVous getRendezVous() {
        return rendezVous;
    }

    public void setRendezVous(RendezVous rendezVous) {
        this.rendezVous = rendezVous;
    }

    public Conduite getConduite() {
        return conduite;
    }

    public void setConduite(Conduite conduite) {
        this.conduite = conduite;
    }

    public Consultation() {

    }

    public Consultation(Long idConsultation, LocalDate dateConsultation, EStatusConsultation statusConsultation, String resultatExamenClinique, String resultatExamenParacliniques, String diagnosticMedical, String traitement, String motifCnsultation, Client client, Patient patient, List<Document> documents, Conduite conduite, RendezVous rendezVous, List<Traitement> traitements) {
        this.idConsultation = idConsultation;
        this.dateConsultation = dateConsultation;
        this.statusConsultation = statusConsultation;
        this.resultatExamenClinique = resultatExamenClinique;
        this.resultatExamenParacliniques = resultatExamenParacliniques;
        this.diagnosticMedical = diagnosticMedical;
        this.traitement = traitement;
        this.motifCnsultation = motifCnsultation;
        this.client = client;
        this.patient = patient;
        this.documents = documents;
        this.conduite = conduite;
        this.rendezVous = rendezVous;
        this.traitements = traitements;
    }

    public String getTraitement() {
        return traitement;
    }

    public void setTraitement(String traitement) {
        this.traitement = traitement;
    }

    public String getMotifCnsultation() {
        return motifCnsultation;
    }

    public void setMotifCnsultation(String motifCnsultation) {
        this.motifCnsultation = motifCnsultation;
    }

    public EStatusConsultation getStatusConsultation() {
        return statusConsultation;
    }

    public void setStatusConsultation(EStatusConsultation statusConsultation) {
        this.statusConsultation = statusConsultation;
    }


    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public String getMotif() {
        return motifCnsultation;
    }

    public void setMotif(String motif) {
        this.motifCnsultation = motif;
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

    public List<Traitement> getTraitements() {
        return traitements;
    }

    public void setTraitements(List<Traitement> traitements) {
        this.traitements = traitements;
    }

    public String getDiagnosticMedical() {
        return diagnosticMedical;
    }

    public void setDiagnosticMedical(String diagnosticMedical) {
        this.diagnosticMedical = diagnosticMedical;
    }
}
