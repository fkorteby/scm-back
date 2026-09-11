package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table
public class ImpressionPub {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idImpressionPub;

    @Column(nullable = false)
    private LocalDateTime dateHeure;

    private Long idCampagnePub;

    private Long idUtilisateur;

    private boolean click;
}
