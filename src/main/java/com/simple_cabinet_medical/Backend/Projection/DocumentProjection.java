package com.simple_cabinet_medical.Backend.Projection;

import com.simple_cabinet_medical.Backend.model.Document;
import com.simple_cabinet_medical.Backend.model.ETypeDocument;
import org.springframework.data.rest.core.config.Projection;

import java.time.LocalDate;

@Projection(name = "documents", types = Document.class)
public interface DocumentProjection {
    Long getIdDocument();
    Long getIdUtilisateur();

    ETypeDocument getTypeDocument();

    String getNomDocument();

    LocalDate getDateGeneration();

    String getText();

    PatientInfo getPatient();
}
