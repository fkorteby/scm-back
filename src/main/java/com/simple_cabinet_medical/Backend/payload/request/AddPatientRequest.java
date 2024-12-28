package com.simple_cabinet_medical.Backend.payload.request;

public class AddPatientRequest {

    private String nom;
    private String prenom;
    private int age;
    private String dateNaissance;
    private String telephone;
    private String sexe;
    private String situationFamiliale;
    private String profession;

    private Boolean isAssure;
    private String adresse;
    private String antecedentsPersonnelsMedicaux;
    private String antecedentsPersonnelsChirugicaux;
    private String antecedentsFamiliaux;
    private String autres;

    public AddPatientRequest() {}

    public AddPatientRequest(String nom, String prenom, int age, String dateNaissance, String telephone, String sexe, String situationFamiliale, String profession, Boolean isAssure, String adresse, String antecedentsPersonnelsMedicaux, String antecedentsPersonnelsChirugicaux, String antecedentsFamiliaux, String autres) {
        this.nom = nom;
        this.prenom = prenom;
        this.age = age;
        this.dateNaissance = dateNaissance;
        this.telephone = telephone;
        this.sexe = sexe;
        this.situationFamiliale = situationFamiliale;
        this.profession = profession;
        this.isAssure = isAssure;
        this.adresse = adresse;
        this.antecedentsPersonnelsMedicaux = antecedentsPersonnelsMedicaux;
        this.antecedentsPersonnelsChirugicaux = antecedentsPersonnelsChirugicaux;
        this.antecedentsFamiliaux = antecedentsFamiliaux;
        this.autres = autres;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getSituationFamiliale() {
        return situationFamiliale;
    }

    public void setSituationFamiliale(String situationFamiliale) {
        this.situationFamiliale = situationFamiliale;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public Boolean getAssure() {
        return isAssure;
    }

    public void setAssure(Boolean assure) {
        isAssure = assure;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
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
}
