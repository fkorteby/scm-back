package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Duree {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @NotNull
    private Long idDuree;

    @NotNull
    private String duree;

    @NotNull
    private Long idUtilisateur;

    @NotNull
    private Date dateCreation;


}
