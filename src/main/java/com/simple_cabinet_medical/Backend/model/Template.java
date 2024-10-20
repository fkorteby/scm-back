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
public class Template {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long idTemplate;

    private String nom;

    private String text;

    private Long idUtilisateur;

}
