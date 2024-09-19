package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Medicament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicamentRepository extends JpaRepository<Medicament, Long> {

}
