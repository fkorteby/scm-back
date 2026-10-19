package com.simple_cabinet_medical.Backend.dto.adminDash;

import java.util.List;

public record DoctorEvolutionDto(
        String title,
        String subtitle,
        List<String> labels,
        List<Long> data
) {
}