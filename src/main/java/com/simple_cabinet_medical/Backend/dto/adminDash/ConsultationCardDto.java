package com.simple_cabinet_medical.Backend.dto.adminDash;

public record ConsultationCardDto(
        long totalConsultations,
        String title,
        long consultationsToday
) {
}
