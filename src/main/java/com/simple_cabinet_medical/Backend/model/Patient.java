package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Patient {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long idPatient;

    private String nom;

    private String prenom;

    private String dateNaissance;

    private String numeroTel;

    private String situation;

    private String sexe;

    private String adresse;

    private String profession;

    private Boolean assurance;

    private String antecedentsPersonnelsMedicaux;

    private String antecedentsPersonnelsChirugicaux;

    private String antecedentsFamiliaux;

    private String autres;

    @OneToMany (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //@JoinColumn(name = "patient_id")
    private List<Consultation> consultations;

    @OneToMany (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //@JoinColumn (name = "patient_id")
    private Set<Document> documents;


    @ManyToOne()
    private Utilisateur utilisateur;

    private Date dateCreation;

}
