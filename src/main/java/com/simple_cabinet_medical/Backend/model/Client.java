package com.simple_cabinet_medical.Backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idClient;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String nomClient;
    @Column(unique = true)
    private String nomClientEnArabe;
    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true)
    private String contact;
    @Column(unique = true)
    private String adresse;
    private String pays;
    private String ville;
    private String codePostal;
    private String rue;
    @Column(unique = true)
    private String telephone;
    @Column(unique = true)
    private String mobile;
    @Column(unique = true)
    private String site;
    @Column(unique = true)
    private String numeroAgrement;
    @Column(columnDefinition = "TEXT")
    private String description;
    @NotNull
    @Enumerated(EnumType.STRING)
    private EStatus status;

    private String image;

    private String specialite;

    @Enumerated(EnumType.STRING)
    private EmailVerificationStatus emailVerificationStatus;

    @CreatedDate
    protected Date dateCreation;

    @OneToMany(mappedBy = "client",cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Local> locals  = new HashSet<>();;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "idClient")
    @JsonIgnore
    private Set<Utilisateur> utilisateurs;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id")
    @JsonIgnore
    @RestResource(exported = false)
    private Set<Patient> patients;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Consultation> consultations;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "client_config_id",referencedColumnName = "idClientConfig")
    private ClientConfig clientConfig;

    public Client(Long idClient, String nomClient, String nomClientEnArabe, String email, String contact, String adresse, String pays, String ville, String codePostal, String rue, String telephone, String mobile, String site, String numeroAgrement, String description, EStatus status, String image, String specialite, EmailVerificationStatus emailVerificationStatus, Set<Local> locals, Set<Utilisateur> utilisateurs, Set<Patient> patients, Set<Consultation> consultations, ClientConfig clientConfig) {
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
        this.numeroAgrement = numeroAgrement;
        this.description = description;
        this.status = status;
        this.image = image;
        this.specialite = specialite;
        this.emailVerificationStatus = emailVerificationStatus;
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

    public String getNumeroAgrement() {
        return numeroAgrement;
    }

    public void setNumeroAgrement(String numeroAgrement) {
        this.numeroAgrement = numeroAgrement;
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

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public EmailVerificationStatus getEmailVerificationStatus() {
        return emailVerificationStatus;
    }

    public void setEmailVerificationStatus(EmailVerificationStatus emailVerificationStatus) {
        this.emailVerificationStatus = emailVerificationStatus;
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
}
