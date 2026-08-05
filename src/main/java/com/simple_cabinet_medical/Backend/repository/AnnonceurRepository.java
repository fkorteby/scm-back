package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.Projection.Pub.AnnonceurProjection;
import com.simple_cabinet_medical.Backend.model.Annonceur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Optional;

@RepositoryRestResource(excerptProjection = AnnonceurProjection.class)
public interface AnnonceurRepository extends JpaRepository<Annonceur, Long> {
    @RestResource(path = "by-nom")
    Page<Annonceur> findByNomAnnonceurContainingIgnoreCase(String nomAnnonceur, Pageable pageable);

    Optional<Annonceur> findByUtilisateur_IdUtilisateur(Long idUtilisateur);
}
