package com.simple_cabinet_medical.Backend.Dto.adminDash;

public record GeoStatDto(
        String city,
        String country,
        long doctorCount,
        long cabinetCount,
        double percentage
) {
}