package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;
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

    @OneToOne
    private Traitement traitement;

}
