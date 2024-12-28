package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
public class Template {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long idTemplate;

    private String nom;

    private String text;

    private Long idUtilisateur;

    public Template(Long idTemplate, String nom, String text, Long idUtilisateur) {
        this.idTemplate = idTemplate;
        this.nom = nom;
        this.text = text;
        this.idUtilisateur = idUtilisateur;
    }

    public Template() {}


    public Long getIdTemplate() {
        return idTemplate;
    }

    public void setIdTemplate(Long idTemplate) {
        this.idTemplate = idTemplate;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Long idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }
}
