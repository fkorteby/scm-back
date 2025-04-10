package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Set;

@Entity
public class Patient extends BasedObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPatient;

    @NonNull
    private String nom;

    @NonNull
    private String prenom;

    private String dateNaissance;

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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "patient_id")
    private List<Consultation> consultations;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "patient_id")
    private Set<Document> documents;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Patient() {
    }

    public Long getIdPatient() {
        return idPatient;
    }

    public void setIdPatient(Long idPatient) {
        this.idPatient = idPatient;
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

    public String getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(String dateNaissance) {
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

    public List<Consultation> getConsultations() {
        return consultations;
    }

    public void setConsultations(List<Consultation> consultations) {
        this.consultations = consultations;
    }

    public Set<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
    }

}
