package com.simple_cabinet_medical.Backend.Projection;

import java.time.LocalDate;

public interface PatientInfo {
    Long getIdPatient();

    String getNom();

    String getPrenom();

    String getSituation();

    LocalDate getDateNaissance();

    String getNumeroTel();

    Boolean getAssurance();

    String getAntecedentsPersonnelsMedicaux();

    String getAntecedentsPersonnelsChirugicaux();

    String getAntecedentsFamiliaux();

    String getAutres();
}
