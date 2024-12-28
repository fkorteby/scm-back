package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.repository.TraitementRepository;
import org.springframework.stereotype.Service;

@Service
public class TraitementService {

    private final TraitementRepository traitementRepository;

    public TraitementService(TraitementRepository traitementRepository) {
        this.traitementRepository = traitementRepository;
    }

    public void addTraitement () {}

    public void deleteTraitement () {}

    public void updateTraitement () {}
}
