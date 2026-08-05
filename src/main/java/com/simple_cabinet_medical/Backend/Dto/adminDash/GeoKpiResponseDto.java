package com.simple_cabinet_medical.Backend.Dto.adminDash;

import java.util.List;

public record GeoKpiResponseDto(
        String title,
        String subtitle,
        List<GeoStatDto> stats
) {
}