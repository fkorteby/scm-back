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
public class Posologie {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long idPosologie;

    private String posologie;

    private Long idUtilisateur;

}
