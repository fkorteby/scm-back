package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Posologie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

@RepositoryRestResource
@PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN')")
@PostFilter("hasPermission(filterObject,'READ')")
public interface PosologieRepository extends JpaRepository<Posologie, Long> {

    @RestResource(path = "byClient")
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Page<Posologie> findAllByClientCreatorId(@Param("clientId") Long clientCreatorId, Pageable pageable);

    @RestResource(path = "byPosologie")
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Page<Posologie> findByClientCreatorIdAndPosologieContainingIgnoreCase(@Param("clientId") Long clientCreatorId,
                                                                          String posologie, Pageable pageable);

    @RestResource(path = "allByPosologie")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Page<Posologie> findByPosologieContainingIgnoreCase(String posologie, Pageable pageable);

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Optional<Posologie> findById(Long aLong);
}
