package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Conduite;
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
public interface ConduiteRepository extends JpaRepository<Conduite, Long> {

    @RestResource(path = "byClient")
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Page<Conduite> findAllByClientCreatorId(@Param("clientId") Long clientCreatorId, Pageable pageable);

    @RestResource(path = "byConduite")
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Page<Conduite> findByClientCreatorIdAndConduiteContainingIgnoreCase(@Param("clientId") Long clientCreatorId, String conduite, Pageable pageable);

    @RestResource(path = "AllByConduite")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Page<Conduite> findByConduiteContainingIgnoreCase(String conduite, Pageable pageable);

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Optional<Conduite> findById(Long aLong);
}
