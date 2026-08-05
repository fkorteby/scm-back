package com.simple_cabinet_medical.Backend.Dto;

public record RapportSummaryPatientsDTO (
        Long totalPatients,
        Long males,
        Long females,
        Long insured,
        Long uninsured
) {}