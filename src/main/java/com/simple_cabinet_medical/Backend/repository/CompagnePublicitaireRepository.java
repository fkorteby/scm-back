package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.Projection.Pub.CompagnePublicitaireProjection;
import com.simple_cabinet_medical.Backend.model.CompagnePublicitaire;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(excerptProjection = CompagnePublicitaireProjection.class)
public interface CompagnePublicitaireRepository extends JpaRepository<CompagnePublicitaire, Long> {

    @RestResource(path = "by-annonceur", rel = "by-annonceur")
    Page<CompagnePublicitaire> findAllByAnnonceur_IdAnnonceur(
            @Param("id") Long id,
            Pageable pageable
    );

    @RestResource(path = "by-compagne", rel = "by-compagne")
    Page<CompagnePublicitaire> findAllByAnnonceur_IdAnnonceurAndNomCompagnePubContainingIgnoreCase(
            @Param("id") Long id,
            @Param("compagne") String nomCompagnePub,
            Pageable pageable
    );
}
