package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets_support")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomDemandeur;

    @Column(nullable = false)
    private String emailDemandeur;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String module;

    @Column(nullable = false)
    private String sujet;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    private String etablissementNom;
    private String medecinTelephone;
    private String userAgent;
    private String pieceJointePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status = TicketStatus.OUVERT;

    private LocalDateTime dateOuverture = LocalDateTime.now();

    public Ticket() {}

    public Ticket(Long id, String nomDemandeur, String emailDemandeur, String type, String module, String sujet, String description, String etablissementNom, String medecinTelephone, String userAgent, String pieceJointePath, TicketStatus status, LocalDateTime dateOuverture) {
        this.id = id;
        this.nomDemandeur = nomDemandeur;
        this.emailDemandeur = emailDemandeur;
        this.type = type;
        this.module = module;
        this.sujet = sujet;
        this.description = description;
        this.etablissementNom = etablissementNom;
        this.medecinTelephone = medecinTelephone;
        this.userAgent = userAgent;
        this.pieceJointePath = pieceJointePath;
        this.status = status;
        this.dateOuverture = dateOuverture;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomDemandeur() {
        return nomDemandeur;
    }

    public void setNomDemandeur(String nomDemandeur) {
        this.nomDemandeur = nomDemandeur;
    }

    public String getEmailDemandeur() {
        return emailDemandeur;
    }

    public void setEmailDemandeur(String emailDemandeur) {
        this.emailDemandeur = emailDemandeur;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEtablissementNom() {
        return etablissementNom;
    }

    public void setEtablissementNom(String etablissementNom) {
        this.etablissementNom = etablissementNom;
    }

    public String getMedecinTelephone() {
        return medecinTelephone;
    }

    public void setMedecinTelephone(String medecinTelephone) {
        this.medecinTelephone = medecinTelephone;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getPieceJointePath() {
        return pieceJointePath;
    }

    public void setPieceJointePath(String pieceJointePath) {
        this.pieceJointePath = pieceJointePath;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public LocalDateTime getDateOuverture() {
        return dateOuverture;
    }

    public void setDateOuverture(LocalDateTime dateOuverture) {
        this.dateOuverture = dateOuverture;
    }
}