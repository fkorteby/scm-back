package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;
import java.util.Date;
import java.util.Set;


@Entity
public class Ordonnance {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long idOrdonnance;

    @OneToOne (mappedBy = "ordonnance")
    private Consultation consultation;

    @OneToMany ( cascade = CascadeType.ALL  ,fetch = FetchType.LAZY)
    @JoinColumn (name = "ordonnace_id")
    private Set<Traitement> traitements;

    private Long idUtilisateur;

    private Date dateCreation;

    private Date dateModification;

    public Ordonnance() {}

    public Long getIdOrdonnance() {
        return idOrdonnance;
    }

    public void setIdOrdonnance(Long idOrdonnance) {
        this.idOrdonnance = idOrdonnance;
    }

    public Consultation getConsultation() {
        return consultation;
    }

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
    }

    public Set<Traitement> getTraitements() {
        return traitements;
    }

    public void setTraitements(Set<Traitement> traitements) {
        this.traitements = traitements;
    }

    public Long getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Long idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Date getDateModification() {
        return dateModification;
    }

    public void setDateModification(Date dateModification) {
        this.dateModification = dateModification;
    }
}
