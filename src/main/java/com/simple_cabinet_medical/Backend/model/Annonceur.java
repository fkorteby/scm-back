package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;

import java.util.List;

@Table
@Entity
public class Annonceur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAnnonceur;
    @Column(nullable = false,unique = true)
    private String nomAnnonceur;
    private String raisonSociale;
    @Column(unique = true)
    private String numeroRegistreCommerce;
    @Column(unique = true)
    private String nif;
    @Column(unique = true)
    private String nis;
    @Column(unique = true)
    private String rib;
    private String adresseLegale;
    @Enumerated(EnumType.STRING)
    private ETypeAnnonceur typeAnnonceur;
    private String SecteurActivite;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EStatusAnnonceur status;
    private String telephoneContact;
    @OneToMany(mappedBy = "annonceur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompagnePublicitaire> compagnePublicitaires;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "idUtilisateur", unique = true)
    private Utilisateur utilisateur;

    public Annonceur() {
    }

    public Annonceur(Long idAnnonceur, String nomAnnonceur, String raisonSociale, String numeroRegistreCommerce, String nif, String nis, String rib, String adresseLegale, ETypeAnnonceur typeAnnonceur, String secteurActivite, EStatusAnnonceur status, String telephoneContact, List<CompagnePublicitaire> compagnePublicitaires, Utilisateur utilisateur) {
        this.idAnnonceur = idAnnonceur;
        this.nomAnnonceur = nomAnnonceur;
        this.raisonSociale = raisonSociale;
        this.numeroRegistreCommerce = numeroRegistreCommerce;
        this.nif = nif;
        this.nis = nis;
        this.rib = rib;
        this.adresseLegale = adresseLegale;
        this.typeAnnonceur = typeAnnonceur;
        SecteurActivite = secteurActivite;
        this.status = status;
        this.telephoneContact = telephoneContact;
        this.compagnePublicitaires = compagnePublicitaires;
        this.utilisateur = utilisateur;
    }

    public Long getIdAnnonceur() {
        return idAnnonceur;
    }

    public void setIdAnnonceur(Long idAnnonceur) {
        this.idAnnonceur = idAnnonceur;
    }

    public String getNomAnnonceur() {
        return nomAnnonceur;
    }

    public void setNomAnnonceur(String nomAnnonceur) {
        this.nomAnnonceur = nomAnnonceur;
    }

    public String getRaisonSociale() {
        return raisonSociale;
    }

    public void setRaisonSociale(String raisonSociale) {
        this.raisonSociale = raisonSociale;
    }

    public String getNumeroRegistreCommerce() {
        return numeroRegistreCommerce;
    }

    public void setNumeroRegistreCommerce(String numeroRegistreCommerce) {
        this.numeroRegistreCommerce = numeroRegistreCommerce;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getNis() {
        return nis;
    }

    public void setNis(String nis) {
        this.nis = nis;
    }

    public String getRib() {
        return rib;
    }

    public void setRib(String rib) {
        this.rib = rib;
    }

    public String getAdresseLegale() {
        return adresseLegale;
    }

    public void setAdresseLegale(String adresseLegale) {
        this.adresseLegale = adresseLegale;
    }

    public ETypeAnnonceur getTypeAnnonceur() {
        return typeAnnonceur;
    }

    public void setTypeAnnonceur(ETypeAnnonceur typeAnnonceur) {
        this.typeAnnonceur = typeAnnonceur;
    }

    public String getSecteurActivite() {
        return SecteurActivite;
    }

    public void setSecteurActivite(String secteurActivite) {
        SecteurActivite = secteurActivite;
    }

    public EStatusAnnonceur getStatus() {
        return status;
    }

    public void setStatus(EStatusAnnonceur status) {
        this.status = status;
    }

    public List<CompagnePublicitaire> getCompagnePublicitaires() {
        return compagnePublicitaires;
    }

    public void setCompagnePublicitaires(List<CompagnePublicitaire> compagnePublicitaires) {
        this.compagnePublicitaires = compagnePublicitaires;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getTelephoneContact() {
        return telephoneContact;
    }

    public void setTelephoneContact(String telephoneContact) {
        this.telephoneContact = telephoneContact;
    }
}
