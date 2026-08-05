package com.simple_cabinet_medical.Backend.Dto.Dash;

import java.time.LocalDate;

public class ConsultationParJourDto {
    private LocalDate jour;
    private Long nbConsultations;

    public ConsultationParJourDto(LocalDate jour, Long nbConsultations) {
        this.jour = jour;
        this.nbConsultations = nbConsultations;
    }
    public LocalDate getJour() {
        return jour;
    }
    public void setJour(LocalDate jour) {
        this.jour = jour;
    }
    public Long getNbConsultations() {
        return nbConsultations;
    }
    public void setNbConsultations(Long nbConsultations) {
        this.nbConsultations = nbConsultations;
    }
}
