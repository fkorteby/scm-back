package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class OptionParaclinique {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long idOptionParaclinique;

    private String option;

    private Long idUtilisateur;

}
