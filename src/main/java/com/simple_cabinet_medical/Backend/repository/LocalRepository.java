package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalRepository extends JpaRepository<Local, Long> {
}
