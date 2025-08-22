package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Patient extends BasedObject implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPatient;

    @NonNull
    private String nom;

    @NonNull
    private String prenom;

    private LocalDate dateNaissance;

    private String numeroTel;

    private String situation;

    private String sexe;

    private String adresse;

    private String profession;

    private Boolean assurance;

    @Lob
    private String antecedentsPersonnelsMedicaux;

    @Lob
    private String antecedentsPersonnelsChirugicaux;

    @Lob
    private String antecedentsFamiliaux;

    @Lob
    private String autres;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Consultation> consultations;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Document> documents;


    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<RendezVous> rendezVous;

    public Patient(Long idPatient, @NonNull String nom, @NonNull String prenom, LocalDate dateNaissance, String numeroTel, String situation, String sexe, String adresse, String profession, Boolean assurance, String antecedentsPersonnelsMedicaux, String antecedentsPersonnelsChirugicaux, String antecedentsFamiliaux, String autres, List<Consultation> consultations, List<Document> documents, Client client, List<RendezVous> rendezVous) {
        this.idPatient = idPatient;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.numeroTel = numeroTel;
        this.situation = situation;
        this.sexe = sexe;
        this.adresse = adresse;
        this.profession = profession;
        this.assurance = assurance;
        this.antecedentsPersonnelsMedicaux = antecedentsPersonnelsMedicaux;
        this.antecedentsPersonnelsChirugicaux = antecedentsPersonnelsChirugicaux;
        this.antecedentsFamiliaux = antecedentsFamiliaux;
        this.autres = autres;
        this.consultations = consultations;
        this.documents = documents;
        this.client = client;
        this.rendezVous = rendezVous;
    }

    public Patient() {

    }

    public Long getIdPatient() {
        return idPatient;
    }

    public void setIdPatient(Long idPatient) {
        this.idPatient = idPatient;
    }

    @NonNull
    public String getNom() {
        return nom;
    }

    public void setNom(@NonNull String nom) {
        this.nom = nom;
    }

    @NonNull
    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(@NonNull String prenom) {
        this.prenom = prenom;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getNumeroTel() {
        return numeroTel;
    }

    public void setNumeroTel(String numeroTel) {
        this.numeroTel = numeroTel;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public Boolean getAssurance() {
        return assurance;
    }

    public void setAssurance(Boolean assurance) {
        this.assurance = assurance;
    }

    public String getAntecedentsPersonnelsMedicaux() {
        return antecedentsPersonnelsMedicaux;
    }

    public void setAntecedentsPersonnelsMedicaux(String antecedentsPersonnelsMedicaux) {
        this.antecedentsPersonnelsMedicaux = antecedentsPersonnelsMedicaux;
    }

    public String getAntecedentsPersonnelsChirugicaux() {
        return antecedentsPersonnelsChirugicaux;
    }

    public void setAntecedentsPersonnelsChirugicaux(String antecedentsPersonnelsChirugicaux) {
        this.antecedentsPersonnelsChirugicaux = antecedentsPersonnelsChirugicaux;
    }

    public String getAntecedentsFamiliaux() {
        return antecedentsFamiliaux;
    }

    public void setAntecedentsFamiliaux(String antecedentsFamiliaux) {
        this.antecedentsFamiliaux = antecedentsFamiliaux;
    }

    public String getAutres() {
        return autres;
    }

    public void setAutres(String autres) {
        this.autres = autres;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Consultation> getConsultations() {
        return consultations;
    }

    public void setConsultations(List<Consultation> consultations) {
        this.consultations = consultations;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public List<RendezVous> getRendezVous() {
        return rendezVous;
    }

    public void setRendezVous(List<RendezVous> rendezVous) {
        this.rendezVous = rendezVous;
    }
}
