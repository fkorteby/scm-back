package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;

import java.util.Set;


@Entity
public class OrdonnanceType extends BasedObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOrdonnanceType;

    private String name;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "ordonnance_Type_id",referencedColumnName = "idOrdonnanceType")
    private Set<Traitement> traitements;

    public OrdonnanceType(Long idOrdonnanceType, String name, Set<Traitement> traitements) {
        this.idOrdonnanceType = idOrdonnanceType;
        this.name = name;
        this.traitements = traitements;
    }

    public OrdonnanceType() {

    }

    public Long getIdOrdonnanceType() {
        return idOrdonnanceType;
    }

    public void setIdOrdonnanceType(Long idOrdonnanceType) {
        this.idOrdonnanceType = idOrdonnanceType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Traitement> getTraitements() {
        return traitements;
    }

    public void setTraitements(Set<Traitement> traitements) {
        this.traitements = traitements;
    }
}
