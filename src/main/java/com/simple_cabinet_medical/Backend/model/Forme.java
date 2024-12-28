package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Forme {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long idForme;

    @NotNull
    private String forme;

    @NotNull
    private String abreviation;

    @NotNull
    private Long idUtilisateur;

    public Forme (String forme, String abreviation, Long idUtilisateur) {
        this.forme = forme;
        this.abreviation = abreviation;
        this.idUtilisateur = idUtilisateur;
    }

}
