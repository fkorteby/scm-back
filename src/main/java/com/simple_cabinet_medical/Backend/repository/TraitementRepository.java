package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Traitement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TraitementRepository extends JpaRepository<Traitement, Long> {
}
