package com.simple_cabinet_medical.Backend.service.MdbImport;

import com.simple_cabinet_medical.Backend.model.Patient;
import com.simple_cabinet_medical.Backend.service.MdbImport.ImportRowPersister.ConsultationUnit;

import java.util.List;

/**
 * Un patient prêt à être persisté avec toute son arborescence
 * (consultations + traitements déjà résolus), produit par le reader,
 * consommé par le writer. C'est l'unité de "chunk" de notre step Spring Batch.
 */
public record PatientImportUnit(long numMal, Patient patient, List<ConsultationUnit> consultationUnits) {
}