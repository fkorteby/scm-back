package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;

@Entity
public class Traitement extends BasedObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTraitement;

    @ManyToOne
    @JoinColumn(name = "medicament_id", referencedColumnName = "idMedicament")
    private Medicament medicament;

    private String duree;

    private String posologie;

    @ManyToOne
    @JoinColumn(name = "idConsultation")
    private Consultation consultation;

    @ManyToOne
    @JoinColumn(name = "ordonnance_type_id", referencedColumnName = "idOrdonnanceType")
    private OrdonnanceType ordonnanceType;

    public Traitement(Long idTraitement, Medicament medicament, String duree, String posologie, Consultation consultation, OrdonnanceType ordonnanceType) {
        this.idTraitement = idTraitement;
        this.medicament = medicament;
        this.duree = duree;
        this.posologie = posologie;
        this.consultation = consultation;
        this.ordonnanceType = ordonnanceType;
    }

    public OrdonnanceType getOrdonnanceType() {
        return ordonnanceType;
    }

    public void setOrdonnanceType(OrdonnanceType ordonnanceType) {
        this.ordonnanceType = ordonnanceType;
    }

    public void setIdTraitement(Long idTraitement) {
        this.idTraitement = idTraitement;
    }

    public Consultation getConsultation() {
        return consultation;
    }

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
    }

    public Traitement() {

    }

    public Long getIdTraitement() {
        return idTraitement;
    }

    public void setIdTr(Long idTraitement) {
        this.idTraitement = idTraitement;
    }

    public Medicament getMedicament() {
        return medicament;
    }

    public void setMedicament(Medicament medicament) {
        this.medicament = medicament;
    }

    public String getDuree() {
        return duree;
    }

    public void setDuree(String duree) {
        this.duree = duree;
    }

    public String getPosologie() {
        return posologie;
    }

    public void setPosologie(String posologie) {
        this.posologie = posologie;
    }
}
