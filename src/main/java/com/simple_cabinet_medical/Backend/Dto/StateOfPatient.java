package com.simple_cabinet_medical.Backend.Dto;

public record StateOfPatient(
        int numberOfConsultations,
        int numberOfDocuments,
        int numberOfAppointments
) {
}