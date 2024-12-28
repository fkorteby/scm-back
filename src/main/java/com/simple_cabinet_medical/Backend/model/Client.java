package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;


@Entity
public class Client {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @NotNull
    private Long idClient;

    @NotNull
    private String nom;

    @OneToMany (cascade = CascadeType.ALL , fetch = FetchType.LAZY)
    @JoinColumn (name = "client_id")
    private Set<Local> locals;

    @OneToMany (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn (name = "client_id")
    private Set<Utilisateur> utilisateurs;

    @OneToMany (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn (name = "client_id")
    private Set<Patient> patients;

    private Long idUtilisateur;

    private Date dateCreation;

    public Client(Long id, String nom, Set<Local> locals, Set<Utilisateur> utilisateurs, Set<Patient> patients, Long idUtilisateur, Date dateCreation) {
        this.idClient = id;
        this.nom = nom;
        this.locals = locals;
        this.utilisateurs = utilisateurs;
        this.patients = patients;
        this.idUtilisateur = idUtilisateur;
        this.dateCreation = dateCreation;
    }

    public Client () {}

    public Long getId() {
        return idClient;
    }

    public void setId(Long id) {
        this.idClient = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
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

    public Long getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Long idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }
}
