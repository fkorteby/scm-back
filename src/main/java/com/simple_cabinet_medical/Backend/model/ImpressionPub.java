package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "impression_pub")
public class ImpressionPub {
    @Id
    private Long idImpressionPub;

    @Column(nullable = false)
    private LocalDateTime dateHeure;

    private Long idCampagnePub;

    private Long idUtilisateur;

    private boolean click;
}
