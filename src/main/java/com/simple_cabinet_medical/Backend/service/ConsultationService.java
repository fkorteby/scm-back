package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.repository.ConsultationRepository;
import org.springframework.stereotype.Service;

@Service
public class ConsultationService {

    private final ConsultationRepository consultationRepository;

    public ConsultationService(ConsultationRepository consultationRepository) {
        this.consultationRepository = consultationRepository;
    }

    public void addConsultation () {}

    public void deleteConsultation () {}

    public void updateConsultation () {}

    public void getAllConsultation () {}

    public void findConsultation () {}


}
