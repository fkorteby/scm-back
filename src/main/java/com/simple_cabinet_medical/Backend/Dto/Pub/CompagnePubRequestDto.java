package com.simple_cabinet_medical.Backend.Dto.Pub;

import java.time.LocalDate;
import java.util.List;

public class CompagnePubRequestDto {

    private String nomCompagnePub;
    private Long budjet;
    private String status;
    private String description;
    private String type;
    private String formatAffichePub;
    private Long annonceur;
    private Long idCiblage;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String lien;
    private List<String> wilayas;
    private List<String> specialites;

    public CompagnePubRequestDto(String nomCompagnePub, Long budjet, String status, String description, String type, String formatAffichePub, Long annonceur, Long idCiblage, LocalDate dateDebut, LocalDate dateFin, String lien, List<String> wilayas, List<String> specialites) {
        this.nomCompagnePub = nomCompagnePub;
        this.budjet = budjet;
        this.status = status;
        this.description = description;
        this.type = type;
        this.formatAffichePub = formatAffichePub;
        this.annonceur = annonceur;
        this.idCiblage = idCiblage;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lien = lien;
        this.wilayas = wilayas;
        this.specialites = specialites;
    }

    public String getNomCompagnePub() {
        return nomCompagnePub;
    }

    public void setNomCompagnePub(String nomCompagnePub) {
        this.nomCompagnePub = nomCompagnePub;
    }

    public Long getBudjet() {
        return budjet;
    }

    public void setBudjet(Long budjet) {
        this.budjet = budjet;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormatAffichePub() {
        return formatAffichePub;
    }

    public void setFormatAffichePub(String formatAffichePub) {
        this.formatAffichePub = formatAffichePub;
    }

    public Long getAnnonceur() {
        return annonceur;
    }

    public void setAnnonceur(Long annonceur) {
        this.annonceur = annonceur;
    }

    public Long getIdCiblage() {
        return idCiblage;
    }

    public void setIdCiblage(Long idCiblage) {
        this.idCiblage = idCiblage;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public List<String> getWilayas() {
        return wilayas;
    }

    public void setWilayas(List<String> wilayas) {
        this.wilayas = wilayas;
    }

    public List<String> getSpecialites() {
        return specialites;
    }

    public void setSpecialites(List<String> specialites) {
        this.specialites = specialites;
    }

    public String getLien() {
        return lien;
    }

    public void setLien(String lien) {
        this.lien = lien;
    }
}
