package com.simple_cabinet_medical.Backend.dto;

public record StateOfPatient(
        int numberOfConsultations,
        int numberOfDocuments,
        int numberOfAppointments
) {
}