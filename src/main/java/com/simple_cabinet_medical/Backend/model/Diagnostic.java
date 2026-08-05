package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Diagnostic extends BasedObject{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diagnosticId;
    private String diagnostic;
    private String description;

    public Diagnostic(Long diagnosticId, String diagnostic, String description) {
        this.diagnosticId = diagnosticId;
        this.diagnostic = diagnostic;
        this.description = description;
    }

    public Diagnostic() {

    }

    public Long getDiagnosticId() {
        return diagnosticId;
    }

    public void setDiagnosticId(Long diagnosticId) {
        this.diagnosticId = diagnosticId;
    }

    public String getDiagnostic() {
        return diagnostic;
    }

    public void setDiagnostic(String diagnostic) {
        this.diagnostic = diagnostic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
