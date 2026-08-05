package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.ParametreDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParametreDefinitionRepository extends JpaRepository<ParametreDefinition, Long> {
    List<ParametreDefinition> findByType(String type);
}
