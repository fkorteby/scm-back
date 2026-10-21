package com.simple_cabinet_medical.Backend.dto.adminDash;

public record PatientKpiDto(
        long totalPatients,
        String title,
        double growthPercentage,
        long newPatientsToday
) {
}