package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.repository.OrdonnanceRepository;
import org.springframework.stereotype.Service;

@Service
public class OrdonnanceService {

    private final OrdonnanceRepository ordonnanceRepository;

    public OrdonnanceService(OrdonnanceRepository ordonnanceRepository) {
        this.ordonnanceRepository = ordonnanceRepository;
    }

    public void addOrdonnance () {}

    public void updateOrdonnance () {}

    public void deleteOrdonnance () {}
}
