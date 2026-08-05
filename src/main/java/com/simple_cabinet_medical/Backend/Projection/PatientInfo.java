package com.simple_cabinet_medical.Backend.Projection;

import java.time.LocalDate;

public interface PatientInfo {
    Long getIdPatient();

    String getNom();

    String getPrenom();
    Long getIdUtilisateur();
    String getSituation();

    String getSexe();

    String getAdresse();


    LocalDate getDateNaissance();

    String getNumeroTel();

    Boolean getAssurance();

    String getAntecedentsPersonnelsMedicaux();

    String getAntecedentsPersonnelsChirugicaux();

    String getAntecedentsFamiliaux();

    String getAutres();
    String getActeNaissance();
    String getPassport();
    String getCin();
    String getNumeroSecuriteSociale();
}
