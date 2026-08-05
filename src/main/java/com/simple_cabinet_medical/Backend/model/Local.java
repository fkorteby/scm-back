package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;

import java.math.BigInteger;
import java.util.Set;


@Entity
public class Local extends BasedObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLocal;

    private String nomLocal;

    private String nomLocalEnArabe;
    private String adresse;
    private String codePostal;
    private String ville;
    private BigInteger latitude;
    private BigInteger longitude;
    private String displayName;
    private String pays;
    private String telephone;
    private String mobile;

    private EStatus status;

    @ManyToOne
    @JoinColumn(name = "id_client")
    private Client client;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "local_id")
    private Set<Consultation> consultations;

    public Local(Long idLocal, String nomLocal, String nomLocalEnArabe, String adresse, String codePostal, String ville, BigInteger latitude, BigInteger longitude, String displayName, String pays, String telephone, String mobile, EStatus status, Client client, Set<Consultation> consultations) {
        this.idLocal = idLocal;
        this.nomLocal = nomLocal;
        this.nomLocalEnArabe = nomLocalEnArabe;
        this.adresse = adresse;
        this.codePostal = codePostal;
        this.ville = ville;
        this.latitude = latitude;
        this.longitude = longitude;
        this.displayName = displayName;
        this.pays = pays;
        this.telephone = telephone;
        this.mobile = mobile;
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

    public BigInteger getLatitude() {
        return latitude;
    }

    public void setLatitude(BigInteger latitude) {
        this.latitude = latitude;
    }

    public BigInteger getLongitude() {
        return longitude;
    }

    public void setLongitude(BigInteger longitude) {
        this.longitude = longitude;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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
