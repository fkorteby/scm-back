package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;


@Entity
public class Local extends BasedObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLocal;

    @NotBlank
    private String nomLocal;
    @NotBlank
    private String nomLocalEnArabe;
    private String adresse;
    private String codePostal;
    private String ville;
    private String pays;
    private String telephone;
    private String mobile;
    private String locationMaps;
    private EStatus status;

    @ManyToOne
    private Client client;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "local_id")
    private Set<Consultation> consultations;

    public Local(Long idLocal, String nomLocal, String nomLocalEnArabe, String adresse, String codePostal, String ville, String pays, String telephone, String mobile, String locationMaps, EStatus status, Client client, Set<Consultation> consultations) {
        this.idLocal = idLocal;
        this.nomLocal = nomLocal;
        this.nomLocalEnArabe = nomLocalEnArabe;
        this.adresse = adresse;
        this.codePostal = codePostal;
        this.ville = ville;
        this.pays = pays;
        this.telephone = telephone;
        this.mobile = mobile;
        this.locationMaps = locationMaps;
        this.status = status;
        this.client = client;
        this.consultations = consultations;
    }

    public Local() {

    }

    public Long getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Long idLocal) {
        this.idLocal = idLocal;
    }

    public String getNomLocal() {
        return nomLocal;
    }

    public void setNomLocal(String nomLocal) {
        this.nomLocal = nomLocal;
    }

    public String getNomLocalEnArabe() {
        return nomLocalEnArabe;
    }

    public void setNomLocalEnArabe(String nomLocalEnArabe) {
        this.nomLocalEnArabe = nomLocalEnArabe;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
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

    public String getLocationMaps() {
        return locationMaps;
    }

    public void setLocationMaps(String locationMaps) {
        this.locationMaps = locationMaps;
    }

    public EStatus getStatus() {
        return status;
    }

    public void setStatus(EStatus status) {
        this.status = status;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Set<Consultation> getConsultations() {
        return consultations;
    }

    public void setConsultations(Set<Consultation> consultations) {
        this.consultations = consultations;
    }
}
