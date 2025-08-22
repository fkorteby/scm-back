package com.simple_cabinet_medical.Backend.Projection;

import com.simple_cabinet_medical.Backend.model.EStatusRendezVous;
import com.simple_cabinet_medical.Backend.model.RendezVous;
import org.springframework.data.rest.core.config.Projection;

import java.time.LocalDate;

@Projection(name = "RendezVousProjection", types = RendezVous.class)
public interface RendezVousProjection {
    Long getIdRendezVous();

    LocalDate getDateRendezVous();

    String getHeureRendezVous();

    String getNotes();
    EStatusRendezVous getStatusRendezVous();
    PatientInfo getPatient();
}
