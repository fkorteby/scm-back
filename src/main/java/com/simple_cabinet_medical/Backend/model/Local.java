package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;

import java.util.Date;
import java.util.Set;


@Entity
public class Local extends BasedObject{

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long idLocal;

    private String adresse;

    @OneToMany (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn (name = "local_id")
    private Set<Consultation> consultations;

    public Local() {}

    public Long getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Long idLocal) {
        this.idLocal = idLocal;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public Set<Consultation> getConsultations() {
        return consultations;
    }

    public void setConsultations(Set<Consultation> consultations) {
        this.consultations = consultations;
    }

}
