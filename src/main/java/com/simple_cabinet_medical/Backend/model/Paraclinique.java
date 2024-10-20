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
public class Paraclinique {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long idParaclinique;

    private String examen;

    private String type;

    private Long idUtilisateur;

}
