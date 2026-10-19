package com.simple_cabinet_medical.Backend.dto;

public record RapportSummaryPatientsDTO (
        Long totalPatients,
        Long males,
        Long females,
        Long insured,
        Long uninsured
) {}