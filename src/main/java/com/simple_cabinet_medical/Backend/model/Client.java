package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Date;
import java.util.Set;


@Entity
public class Client extends BasedObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idClient;

    @NotBlank
    private String nom;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "idClient")
    private Set<Local> locals;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "idClient")
    private Set<Utilisateur> utilisateurs;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id")
    private Set<Patient> patients;


    public Client(Long id, String nom, Set<Local> locals, Set<Utilisateur> utilisateurs, Set<Patient> patients, Long idUtilisateur, Date dateCreation) {
        this.idClient = id;
        this.nom = nom;
        this.locals = locals;
        this.utilisateurs = utilisateurs;
        this.patients = patients;
        this.idUtilisateur = idUtilisateur;
        this.dateCreation = dateCreation;
    }

    public Client() {
    }

    public Long getIdClient() {
        return idClient;
    }

    public void setIdClient(Long id) {
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

}
