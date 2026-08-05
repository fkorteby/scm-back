package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Specialite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface SpecialiteRepository extends JpaRepository<Specialite, Long> {
    Optional<Specialite> findByLibelle(String nomSpecialite);
}
