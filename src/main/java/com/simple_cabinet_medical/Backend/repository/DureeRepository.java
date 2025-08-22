package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Duree;
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
public interface DureeRepository extends JpaRepository<Duree, Long> {

    @RestResource(path = "byClient")
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Page<Duree> findAllByClientCreatorId(@Param("clientId") Long clientCreatorId, Pageable pageable);

    @RestResource(path = "byDuree")
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Page<Duree> findByClientCreatorIdAndDureeContainingIgnoreCase(@Param("clientId") Long clientCreatorId,
                                                                  String duree, Pageable pageable);

    @RestResource(path = "allByDuree")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Page<Duree> findByDureeContainingIgnoreCase(String duree, Pageable pageable);

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Optional<Duree> findById(Long aLong);
}
