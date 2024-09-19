package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Posologie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PosologieRepository extends JpaRepository<Posologie, Long> {
}
