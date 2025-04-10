package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;


@Entity
public class Traitement extends BasedObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTraitement;

    @OneToOne
    private Medicament medicament;

    @OneToOne
    private Posologie posologie;

    public Traitement() {
    }

    public Long getIdTraitement() {
        return idTraitement;
    }

    public void setIdTraitement(Long idTraitement) {
        this.idTraitement = idTraitement;
    }

    public Medicament getMedicament() {
        return medicament;
    }

    public void setMedicament(Medicament medicament) {
        this.medicament = medicament;
    }

    public Posologie getPosologie() {
        return posologie;
    }

    public void setPosologie(Posologie posologie) {
        this.posologie = posologie;
    }

}
