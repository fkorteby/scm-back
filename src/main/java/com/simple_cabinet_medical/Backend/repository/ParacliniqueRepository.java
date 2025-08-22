package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Paraclinique;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
@PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN')")
@PostFilter("hasPermission(filterObject,'READ')")
public interface ParacliniqueRepository extends JpaRepository<Paraclinique, Long> {

    @RestResource(path = "byClient")
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Page<Paraclinique> findAllByClientCreatorId(@Param("clientId") Long clientCreatorId, Pageable pageable);

    @RestResource(path = "allByExamen")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Page<Paraclinique> findByExamenContainingIgnoreCase(String examen, Pageable pageable);

    @RestResource(path = "byExamen")
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Page<Paraclinique> findByClientCreatorIdAndExamenContainingIgnoreCase(@Param("clientId") Long clientCreatorId, String examen, Pageable pageable);

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Optional<Paraclinique> findById(Long aLong);
}
