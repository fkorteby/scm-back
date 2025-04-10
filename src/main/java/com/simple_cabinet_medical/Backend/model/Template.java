package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;

@Entity
public class Template extends BasedObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTemplate;

    @NonNull
    private String nom;

    @Lob
    private String text;


    public Template(Long idTemplate, String nom, String text, Long idUtilisateur) {
        this.idTemplate = idTemplate;
        this.nom = nom;
        this.text = text;
        this.idUtilisateur = idUtilisateur;
    }

    public Template() {
    }


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

}
