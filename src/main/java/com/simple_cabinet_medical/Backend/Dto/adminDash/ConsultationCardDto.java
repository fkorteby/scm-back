package com.simple_cabinet_medical.Backend.Dto.adminDash;

public record ConsultationCardDto(
        long totalConsultations,
        String title,
        long consultationsToday
) {
}
