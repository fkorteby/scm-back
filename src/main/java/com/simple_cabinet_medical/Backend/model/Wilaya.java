package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table
@Entity
public class Wilaya {
    @Id
    private Long numWilaya;
    @Column(nullable = false,unique = true)
    private String nomWilaya;

    public Wilaya() {
    }

    public Wilaya(Long numWilaya, String nomWilaya) {
        this.numWilaya = numWilaya;
        this.nomWilaya = nomWilaya;
    }

    public Long getNumWilaya() {
        return numWilaya;
    }

    public void setNumWilaya(Long numWilaya) {
        this.numWilaya = numWilaya;
    }

    public String getNomWilaya() {
        return nomWilaya;
    }

    public void setNomWilaya(String nomWilaya) {
        this.nomWilaya = nomWilaya;
    }
}
