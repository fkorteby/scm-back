package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.Instant;

@Entity
public class Token {
    @Id
    private String token;
    private Instant dateExpiration;
    private Long idUtilisateur;
    private Long idClient;

    public Token(String token, Instant dateExpiration, Long idUtilisateur, Long idClient) {
        this.token = token;
        this.dateExpiration = dateExpiration;
        this.idUtilisateur = idUtilisateur;
        this.idClient = idClient;
    }

    public Token() {

    }
}
