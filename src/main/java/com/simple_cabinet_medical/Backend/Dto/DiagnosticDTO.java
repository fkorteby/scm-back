package com.simple_cabinet_medical.Backend.Dto;

public class DiagnosticDTO {
    private String diagnosticMedical;

    public DiagnosticDTO(String diagnosticMedical) {
        this.diagnosticMedical = diagnosticMedical;
    }

    public DiagnosticDTO() {
    }

    public String getDiagnosticMedical() {
        return diagnosticMedical;
    }

    public void setDiagnosticMedical(String diagnosticMedical) {
        this.diagnosticMedical = diagnosticMedical;
    }
}
