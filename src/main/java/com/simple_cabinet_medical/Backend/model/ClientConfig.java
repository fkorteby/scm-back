package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;

@Entity
public class ClientConfig extends BasedObject{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idClientConfig;
    @OneToOne(mappedBy = "clientConfig")
    private Client client;
    private String heureDebutTravail;
    private String heureFinTravail;
    private int dureeRendezVous;

    public ClientConfig(Long idClientConfig, Client client, String heureDebutTravail, String heureFinTravail, int dureeRendezVous) {
        this.idClientConfig = idClientConfig;
        this.client = client;
        this.heureDebutTravail = heureDebutTravail;
        this.heureFinTravail = heureFinTravail;
        this.dureeRendezVous = dureeRendezVous;
    }

    public ClientConfig() {

    }

    public Long getIdClientConfig() {
        return idClientConfig;
    }

    public void setIdClientConfig(Long idClientConfig) {
        this.idClientConfig = idClientConfig;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getHeureDebutTravail() {
        return heureDebutTravail;
    }

    public void setHeureDebutTravail(String heureDebutTravail) {
        this.heureDebutTravail = heureDebutTravail;
    }

    public String getHeureFinTravail() {
        return heureFinTravail;
    }

    public void setHeureFinTravail(String heureFinTravail) {
        this.heureFinTravail = heureFinTravail;
    }

    public int getDureeRendezVous() {
        return dureeRendezVous;
    }

    public void setDureeRendezVous(int dureeRendezVous) {
        this.dureeRendezVous = dureeRendezVous;
    }
}
