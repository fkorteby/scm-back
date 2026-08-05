package com.simple_cabinet_medical.Backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.data.rest.core.annotation.RestResource;

@Entity
public class ClientConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idClientConfig;

    @OneToOne(mappedBy = "clientConfig")
    @JsonIgnore
    @RestResource(exported = false)
    private Client client;

    @Column(nullable = false)
    private String heureDebutTravail;
    @Column(nullable = false)
    private String heureFinTravail;
    @Column(nullable = false)
    private int dureeRendezVous;
    @Column(nullable = false)
    private boolean useDefaultMedicament = true;

    @Column(nullable = false)
    private boolean useDefaultForms = true;

    @Column(nullable = false)
    private boolean useDefaultDuree = true;

    @Column(nullable = false)
    private boolean useDefaultPosologie = true;

    @Column(nullable = false)
    private boolean useDefaultMotifs = true;

    @Column(nullable = false)
    private boolean useDefaultConduit = true;

    @Column(nullable = false)
    private boolean useDefaultParClinique = true;

    @Column(nullable = false)
    private boolean useDefaultOptionPatClinique = true;

    @Column(columnDefinition = "TEXT")
    private String htmlContent;

    @Column(columnDefinition = "TEXT")
    private String headerConfigJson;

    public ClientConfig() {
    }

    public ClientConfig(Long idClientConfig, Client client, String heureDebutTravail, String heureFinTravail, int dureeRendezVous, boolean useDefaultMedicament, boolean useDefaultForms, boolean useDefaultDuree, boolean useDefaultPosologie, boolean useDefaultMotifs, boolean useDefaultConduit, boolean useDefaultParClinique, boolean useDefaultOptionPatClinique, String htmlContent, String headerConfigJson) {
        this.idClientConfig = idClientConfig;
        this.client = client;
        this.heureDebutTravail = heureDebutTravail;
        this.heureFinTravail = heureFinTravail;
        this.dureeRendezVous = dureeRendezVous;
        this.useDefaultMedicament = useDefaultMedicament;
        this.useDefaultForms = useDefaultForms;
        this.useDefaultDuree = useDefaultDuree;
        this.useDefaultPosologie = useDefaultPosologie;
        this.useDefaultMotifs = useDefaultMotifs;
        this.useDefaultConduit = useDefaultConduit;
        this.useDefaultParClinique = useDefaultParClinique;
        this.useDefaultOptionPatClinique = useDefaultOptionPatClinique;
        this.htmlContent = htmlContent;
        this.headerConfigJson = headerConfigJson;
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

    public boolean isUseDefaultMedicament() {
        return useDefaultMedicament;
    }

    public void setUseDefaultMedicament(boolean useDefaultMedicament) {
        this.useDefaultMedicament = useDefaultMedicament;
    }

    public boolean isUseDefaultForms() {
        return useDefaultForms;
    }

    public void setUseDefaultForms(boolean useDefaultForms) {
        this.useDefaultForms = useDefaultForms;
    }

    public boolean isUseDefaultDuree() {
        return useDefaultDuree;
    }

    public void setUseDefaultDuree(boolean useDefaultDuree) {
        this.useDefaultDuree = useDefaultDuree;
    }

    public boolean isUseDefaultPosologie() {
        return useDefaultPosologie;
    }

    public void setUseDefaultPosologie(boolean useDefaultPosologie) {
        this.useDefaultPosologie = useDefaultPosologie;
    }

    public boolean isUseDefaultMotifs() {
        return useDefaultMotifs;
    }

    public void setUseDefaultMotifs(boolean useDefaultMotifs) {
        this.useDefaultMotifs = useDefaultMotifs;
    }

    public boolean isUseDefaultConduit() {
        return useDefaultConduit;
    }

    public void setUseDefaultConduit(boolean useDefaultConduit) {
        this.useDefaultConduit = useDefaultConduit;
    }

    public boolean isUseDefaultParClinique() {
        return useDefaultParClinique;
    }

    public void setUseDefaultParClinique(boolean useDefaultParClinique) {
        this.useDefaultParClinique = useDefaultParClinique;
    }

    public boolean isUseDefaultOptionPatClinique() {
        return useDefaultOptionPatClinique;
    }

    public void setUseDefaultOptionPatClinique(boolean useDefaultOptionPatClinique) {
        this.useDefaultOptionPatClinique = useDefaultOptionPatClinique;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public String getHeaderConfigJson() {
        return headerConfigJson;
    }

    public void setHeaderConfigJson(String headerConfigJson) {
        this.headerConfigJson = headerConfigJson;
    }
}
