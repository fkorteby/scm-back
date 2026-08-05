package com.simple_cabinet_medical.Backend.Projection;

public interface PatientsRapportProjection {

    Long getIdPatient();
    String getNom();
    String getPrenom();
    java.time.LocalDate getDateNaissance();
    String getNumeroTel();
    String getSituation();
    Boolean getAssurance();
    String getCin();
    String getSexe();
    String getNumeroSecuriteSociale();
    String getPassport();
    String getActeNaissance();
}
