package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class CompagnePublicitaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCompagnePub;
    @Column(nullable = false)
    private String nomCompagnePub;
    @Column(nullable = false)
    private Long budjet;
    @Enumerated(EnumType.STRING)
    private EStatusCompagnePub status;
    @Lob
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ETypeAnnonce type;
    @Column(unique = true)
    private String lien;
    private String filePath;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EFormatAffichePub formatAffichePub;
    @Column(nullable = false)
    private LocalDate dateDebut;
    @Column(nullable = false)
    private LocalDate dateFin;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idAnnonceur")
    private Annonceur annonceur;

    @Column(columnDefinition = "TEXT")
    private String wilayaCible;
    @Column(columnDefinition = "TEXT")
    private String specialiteCible;

    public CompagnePublicitaire() {
    }

    public CompagnePublicitaire(Long idCompagnePub, String nomCompagnePub, Long budjet, EStatusCompagnePub status, String description, ETypeAnnonce type, String lien, String filePath, EFormatAffichePub formatAffichePub, LocalDate dateDebut, LocalDate dateFin, Annonceur annonceur, String wilayaCible, String specialiteCible) {
        this.idCompagnePub = idCompagnePub;
        this.nomCompagnePub = nomCompagnePub;
        this.budjet = budjet;
        this.status = status;
        this.description = description;
        this.type = type;
        this.lien = lien;
        this.filePath = filePath;
        this.formatAffichePub = formatAffichePub;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.annonceur = annonceur;
        this.wilayaCible = wilayaCible;
        this.specialiteCible = specialiteCible;
    }

    public Long getIdCompagnePub() {
        return idCompagnePub;
    }

    public void setIdCompagnePub(Long idCompagnePub) {
        this.idCompagnePub = idCompagnePub;
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

    public EStatusCompagnePub getStatus() {
        return status;
    }

    public void setStatus(EStatusCompagnePub status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ETypeAnnonce getType() {
        return type;
    }

    public void setType(ETypeAnnonce type) {
        this.type = type;
    }

    public String getLien() {
        return lien;
    }

    public void setLien(String lien) {
        this.lien = lien;
    }

    public EFormatAffichePub getFormatAffichePub() {
        return formatAffichePub;
    }

    public void setFormatAffichePub(EFormatAffichePub formatAffichePub) {
        this.formatAffichePub = formatAffichePub;
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

    public Annonceur getAnnonceur() {
        return annonceur;
    }

    public void setAnnonceur(Annonceur annonceur) {
        this.annonceur = annonceur;
    }

    public String getWilayaCible() {
        return wilayaCible;
    }

    public void setWilayaCible(String wilayaCible) {
        this.wilayaCible = wilayaCible;
    }

    public String getSpecialiteCible() {
        return specialiteCible;
    }

    public void setSpecialiteCible(String specialiteCible) {
        this.specialiteCible = specialiteCible;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
