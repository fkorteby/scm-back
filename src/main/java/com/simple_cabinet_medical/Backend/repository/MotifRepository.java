package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Motif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MotifRepository extends JpaRepository<Motif, Long> {
}
