package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

@Entity
public class Client extends BasedObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idClient;

    @NotBlank
    private String nomClient;
    @NotBlank
    private String nomClientEnArabe;
    @NotBlank
    private String email;
    private String contact;
    private String adresse;
    private String telephone;
    private String mobile;
    private String site;
    private String description;
    @NotNull
    private EStatus status;
    private String image;



    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "idClient")
    private Set<Local> locals;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "idClient")
    private Set<Utilisateur> utilisateurs;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id")
    private Set<Patient> patients;
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private Set<Consultation> consultations;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "client_config_id", referencedColumnName = "idClientConfig")
    private ClientConfig clientConfig;

    public Client(Long idClient, String nomClient, String nomClientEnArabe, String email, String contact, String adresse, String telephone, String mobile, String site, String description, EStatus status, String image, Set<Local> locals, Set<Utilisateur> utilisateurs, Set<Patient> patients, Set<Consultation> consultations, ClientConfig clientConfig) {
        this.idClient = idClient;
        this.nomClient = nomClient;
        this.nomClientEnArabe = nomClientEnArabe;
        this.email = email;
        this.contact = contact;
        this.adresse = adresse;
        this.telephone = telephone;
        this.mobile = mobile;
        this.site = site;
        this.description = description;
        this.status = status;
        this.image = image;
        this.locals = locals;
        this.utilisateurs = utilisateurs;
        this.patients = patients;
        this.consultations = consultations;
        this.clientConfig = clientConfig;
    }

    public Client() {

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

    public Set<Local> getLocals() {
        return locals;
    }

    public void setLocals(Set<Local> locals) {
        this.locals = locals;
    }

    public Set<Utilisateur> getUtilisateurs() {
        return utilisateurs;
    }

    public void setUtilisateurs(Set<Utilisateur> utilisateurs) {
        this.utilisateurs = utilisateurs;
    }

    public Set<Patient> getPatients() {
        return patients;
    }

    public void setPatients(Set<Patient> patients) {
        this.patients = patients;
    }

    public Set<Consultation> getConsultations() {
        return consultations;
    }

    public void setConsultations(Set<Consultation> consultations) {
        this.consultations = consultations;
    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }
}
