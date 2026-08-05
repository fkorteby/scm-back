package com.simple_cabinet_medical.Backend.Dto.Pub;

import com.simple_cabinet_medical.Backend.model.ETypeAnnonceur;

public class AnnonceurRequestDto {
    private String nomAnnonceur;
    private String raisonSociale;
    private String numeroRegistreCommerce;
    private String nif;
    private String nis;
    private String rib;
    private String adresseLegale;
    private ETypeAnnonceur typeAnnonceur;
    private String SecteurActivite;
    private String nomContatct;
    private String prenomContatct;
    private String emailContact;
    private String telephoneContact;

    public AnnonceurRequestDto() {
    }

    public AnnonceurRequestDto(String nomAnnonceur, String raisonSociale, String numeroRegistreCommerce, String nif, String nis, String rib, String adresseLegale, ETypeAnnonceur typeAnnonceur, String secteurActivite, String nomContatct, String prenomContatct, String emailContact, String telephoneContact) {
        this.nomAnnonceur = nomAnnonceur;
        this.raisonSociale = raisonSociale;
        this.numeroRegistreCommerce = numeroRegistreCommerce;
        this.nif = nif;
        this.nis = nis;
        this.rib = rib;
        this.adresseLegale = adresseLegale;
        this.typeAnnonceur = typeAnnonceur;
        SecteurActivite = secteurActivite;
        this.nomContatct = nomContatct;
        this.prenomContatct = prenomContatct;
        this.emailContact = emailContact;
        this.telephoneContact = telephoneContact;
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

    public String getNomContatct() {
        return nomContatct;
    }

    public void setNomContatct(String nomContatct) {
        this.nomContatct = nomContatct;
    }

    public String getPrenomContatct() {
        return prenomContatct;
    }

    public void setPrenomContatct(String prenomContatct) {
        this.prenomContatct = prenomContatct;
    }

    public String getEmailContact() {
        return emailContact;
    }

    public void setEmailContact(String emailContact) {
        this.emailContact = emailContact;
    }

    public String getTelephoneContact() {
        return telephoneContact;
    }

    public void setTelephoneContact(String telephoneContact) {
        this.telephoneContact = telephoneContact;
    }
}
