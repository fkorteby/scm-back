package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Traitement {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long idTraitement;

    @OneToOne
    private Medicament medicament;

    @OneToOne
    private Posologie posologie;

    private Long idUtilisateur;

}
