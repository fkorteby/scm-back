package com.simple_cabinet_medical.Backend.Dto.Dash;

public class TopDiganostic {
    private String diagnostic_medical;
    private Long count;
    public TopDiganostic(String diagnostic_medical, Long count) {
        this.diagnostic_medical = diagnostic_medical;
        this.count = count;
    }
    public String getDiagnostic_medical() {
        return diagnostic_medical;
    }
    public Long getCount() {
        return count;
    }
}
