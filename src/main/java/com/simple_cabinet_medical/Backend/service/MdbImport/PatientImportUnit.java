package com.simple_cabinet_medical.Backend.service.MdbImport;

import com.simple_cabinet_medical.Backend.model.Patient;
import com.simple_cabinet_medical.Backend.service.MdbImport.ImportRowPersister.ConsultationUnit;

import java.util.List;

public record PatientImportUnit(long numMal, Patient patient, List<ConsultationUnit> consultationUnits) {
}