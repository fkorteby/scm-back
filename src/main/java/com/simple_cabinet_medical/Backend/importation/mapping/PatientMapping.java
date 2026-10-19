package com.simple_cabinet_medical.Backend.importation.mapping;

import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@JobScope
public class PatientMapping {

    private final Map<Long, Long> mapping = new HashMap<>();

    public void put(Long oldNumMal, Long newPatientId) {
        mapping.put(oldNumMal, newPatientId);
    }

    public Long get(Long oldNumMal) {
        return mapping.get(oldNumMal);
    }

    public boolean contains(Long oldNumMal) {
        return mapping.containsKey(oldNumMal);
    }

    public int size() {
        return mapping.size();
    }

    public void clear() {
        mapping.clear();
    }
}