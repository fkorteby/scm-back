package com.simple_cabinet_medical.Backend.Dto.Dash;

public class ConsultationsParMoisDTO {
    private int annee;
    private int mois;
    private long total;

    public ConsultationsParMoisDTO(int annee, int mois, long total) {
        this.annee = annee;
        this.mois = mois;
        this.total = total;
    }

    public String getMoisNom() {
        return java.time.Month.of(mois).getDisplayName(
                java.time.format.TextStyle.FULL,
                java.util.Locale.FRENCH
        ) + " " + annee;
    }

    public int getAnnee() {
        return annee;
    }

    public int getMois() {
        return mois;
    }

    public long getTotal() {
        return total;
    }
}
