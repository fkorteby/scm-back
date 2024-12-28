package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.util.Date;


@Entity
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private Long idDocument;

    @NotNull
    private String nomDocument;

    @NotNull
    private String text;

    @NotNull
    private Long idUtilisateur;

    @NotNull
    private Date dateCreation;
}
