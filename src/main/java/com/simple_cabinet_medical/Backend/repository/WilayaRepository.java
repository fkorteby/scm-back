package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Wilaya;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface WilayaRepository extends JpaRepository<Wilaya, Long> {
    Optional<Wilaya> findByNomWilaya(String nomWilaya);
}
