package com.simple_cabinet_medical.Backend.Dto;

public class RepartitionSexeDTO {
    private String sexe;
    private long total;

    public RepartitionSexeDTO(String sexe, long total) {
        this.sexe = sexe;
        this.total = total;
    }

    public String getSexe() {
        return sexe;
    }

    public long getTotal() {
        return total;
    }
}
