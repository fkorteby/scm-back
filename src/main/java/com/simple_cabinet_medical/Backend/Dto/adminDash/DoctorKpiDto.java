package com.simple_cabinet_medical.Backend.Dto.adminDash;

public record DoctorKpiDto(
        long totalDoctors,
        long newDoctorsThisWeek,
        long activeDoctors,
        long inactiveDoctors
) {
}