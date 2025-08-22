package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;

@Entity
public class Traitement extends BasedObject{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTraitement;

    @ManyToOne
    @JoinColumn(name = "medicament_id",referencedColumnName = "idMedicament")
    private Medicament medicament;

    @ManyToOne
    @JoinColumn(name = "duree_id",referencedColumnName = "idDuree")
    private Duree duree;

    @ManyToOne
    @JoinColumn(name = "posologie_id",referencedColumnName = "idPosologie")
    private Posologie posologie;

    @ManyToOne
    @JoinColumn(name = "idConsultation")
    private Consultation consultation;

    public Traitement(Long idTraitement, Medicament medicament, Duree duree, Posologie posologie, Consultation consultation) {
        this.idTraitement = idTraitement;
        this.medicament = medicament;
        this.duree = duree;
        this.posologie = posologie;
        this.consultation = consultation;
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

    public Duree getDuree() {
        return duree;
    }

    public void setDuree(Duree duree) {
        this.duree = duree;
    }

    public Posologie getPosologie() {
        return posologie;
    }

    public void setPosologie(Posologie posologie) {
        this.posologie = posologie;
    }
}
