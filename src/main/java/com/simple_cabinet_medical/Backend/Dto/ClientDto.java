package com.simple_cabinet_medical.Backend.Dto;

import com.simple_cabinet_medical.Backend.model.EStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDate;

public class ClientDto {
    private Long idClient;
    private String nomClient;
    private String nomClientEnArabe;
    private String email;
    private String contact;
    private String adresse;
    private String pays;
    private String ville;
    private String codePostal;
    private String rue;
    private String telephone;
    private String mobile;
    private String site;
    private String description;
    @Enumerated(EnumType.STRING)
    private EStatus status;
    private String image;
    private Long dureeRdv;
    private LocalDate heureDebut;
    private LocalDate heureFin;
    private LocationMaps locationMaps;


    public ClientDto() {
    }

    public ClientDto(Long idClient, String nomClient, String nomClientEnArabe, String email, String contact, String adresse, String pays, String ville, String codePostal, String rue, String telephone, String mobile, String site, String description, EStatus status, String image, Long dureeRdv, LocalDate heureDebut, LocalDate heureFin, LocationMaps locationMaps) {
        this.idClient = idClient;
        this.nomClient = nomClient;
        this.nomClientEnArabe = nomClientEnArabe;
        this.email = email;
        this.contact = contact;
        this.adresse = adresse;
        this.pays = pays;
        this.ville = ville;
        this.codePostal = codePostal;
        this.rue = rue;
        this.telephone = telephone;
        this.mobile = mobile;
        this.site = site;
        this.description = description;
        this.status = status;
        this.image = image;
        this.dureeRdv = dureeRdv;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.locationMaps = locationMaps;
    }

    public Long getIdClient() {
        return idClient;
    }

    public void setIdClient(Long idClient) {
        this.idClient = idClient;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EStatus getStatus() {
        return status;
    }

    public void setStatus(EStatus status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public Long getDureeRdv() {
        return dureeRdv;
    }

    public void setDureeRdv(Long dureeRdv) {
        this.dureeRdv = dureeRdv;
    }

    public LocalDate getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(LocalDate heureDebut) {
        this.heureDebut = heureDebut;
    }

    public LocalDate getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(LocalDate heureFin) {
        this.heureFin = heureFin;
    }

    public LocationMaps getLocationMaps() {
        return locationMaps;
    }

    public void setLocationMaps(LocationMaps locationMaps) {
        this.locationMaps = locationMaps;
    }
}
