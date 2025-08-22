package com.simple_cabinet_medical.Backend.Dto;

public class ConsultationsParMoisDTO {
    private String mois;
    private long total;

    public ConsultationsParMoisDTO(int mois, long total) {
        this.mois = java.time.Month.of(mois).getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.FRENCH);
        this.total = total;
    }

    public String getMois() {
        return mois;
    }

    public long getTotal() {
        return total;
    }
}
