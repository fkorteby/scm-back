package com.simple_cabinet_medical.Backend.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class ClientRegister {
    @NotBlank(message = "Le nom du client est obligatoire")
    @Size(max = 100, message = "Le nom du client ne doit pas dépasser 100 caractères")
    private String nomClient;

    @Size(max = 100, message = "Le nom du client en arabe ne doit pas dépasser 100 caractères")
    private String nomClientEnArabe;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 50, message = "Le prénom ne doit pas dépasser 50 caractères")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    private String email;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^[0-9]{8,15}$", message = "Téléphone invalide, 8 à 15 chiffres")
    private String telephone;

    @NotBlank(message = "L'adresse est obligatoire")
    private String adresse;

    private String pays;
    private String wilaya;
    private String ville;
    private String codePostal;
    private String rue;

    private int dureeRdv;
    private String heureDebut;
    private String heureFin;
    private LocationMaps locationMaps;

    @Size(max = 100, message = "La spécialité ne doit pas dépasser 100 caractères")
    private String specialite;

    public ClientRegister(String nomClient, String nomClientEnArabe, String nom, String prenom, String email, String telephone, String adresse, String pays, String wilaya, String ville, String codePostal, String rue, int dureeRdv, String heureDebut, String heureFin, LocationMaps locationMaps, String specialite) {
        this.nomClient = nomClient;
        this.nomClientEnArabe = nomClientEnArabe;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.adresse = adresse;
        this.pays = pays;
        this.wilaya = wilaya;
        this.ville = ville;
        this.codePostal = codePostal;
        this.rue = rue;
        this.dureeRdv = dureeRdv;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.locationMaps = locationMaps;
        this.specialite = specialite;
    }

    public ClientRegister() {

    }

    public String getNomClient() {
        return nomClient;
    }

    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public String getNomClientEnArabe() {
        return nomClientEnArabe;
    }

    public void setNomClientEnArabe(String nomClientEnArabe) {
        this.nomClientEnArabe = nomClientEnArabe;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getRue() {
        return rue;
    }

    public void setRue(String rue) {
        this.rue = rue;
    }

    public int getDureeRdv() {
        return dureeRdv;
    }

    public void setDureeRdv(int dureeRdv) {
        this.dureeRdv = dureeRdv;
    }

    public String getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(String heureDebut) {
        this.heureDebut = heureDebut;
    }

    public String getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(String heureFin) {
        this.heureFin = heureFin;
    }

    public LocationMaps getLocationMaps() {
        return locationMaps;
    }

    public void setLocationMaps(LocationMaps locationMaps) {
        this.locationMaps = locationMaps;
    }

    public String getWilaya() {
        return wilaya;
    }

    public void setWilaya(String wilaya) {
        this.wilaya = wilaya;
    }
}
