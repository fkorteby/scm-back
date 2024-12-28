package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Medicament {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long idMedicament;

    private String nomCommerciale;

    private String dci;

    private String dosage;

    private String conditionnement;

    private String forme;

    private Long idUtilisateur;

    public Medicament(String nomCommerciale, String dci, String dosage, String conditionnement, String forme, Long idUtilisateur) {
        this.nomCommerciale = nomCommerciale;
        this.dci = dci;
        this.dosage = dosage;
        this.conditionnement = conditionnement;
        this.forme = forme;
        this.idUtilisateur = idUtilisateur;
    }
}
