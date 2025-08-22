package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.LettreOrientationType;
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
public interface LettreOrientationTypeRepository extends JpaRepository<LettreOrientationType, Long> {

    @RestResource(path = "byClient")
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Page<LettreOrientationType> findAllByClientCreatorId(@Param("clientId") Long clientCreatorId, Pageable pageable);

    @RestResource(path = "byName")
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Page<LettreOrientationType> findByClientCreatorIdAndNomLettreOrientationContainingIgnoreCase(
            @Param("clientId") Long clientCreatorId, String nomLettreOrientation, Pageable pageable);

    @RestResource(path = "AllbyName")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Page<LettreOrientationType> findByNomLettreOrientationContainingIgnoreCase(String nomLettreOrientation, Pageable pageable);

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Optional<LettreOrientationType> findById(Long aLong);
}
