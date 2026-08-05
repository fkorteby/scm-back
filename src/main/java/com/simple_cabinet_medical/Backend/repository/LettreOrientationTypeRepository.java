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
@PostFilter("hasPermission(filterObject,'READ')")
public interface LettreOrientationTypeRepository extends JpaRepository<LettreOrientationType, Long> {

    @PreAuthorize("((#lettreOrientationType.idLettreOrientation == null or #lettreOrientationType.idLettreOrientation == 0) ? hasPermission(#lettreOrientationType,'WRITE') : hasPermission(#lettreOrientationType,'UPDATE'))")
    LettreOrientationType save(LettreOrientationType lettreOrientationType);

    @RestResource(path = "byClient")
    Page<LettreOrientationType> findAllByClientCreatorId(@Param("clientId") Long clientCreatorId, Pageable pageable);

    @RestResource(path = "byName")
    Page<LettreOrientationType> findByClientCreatorIdAndNomLettreOrientationContainingIgnoreCase(
            @Param("clientId") Long clientCreatorId,@Param("name") String nomLettreOrientation, Pageable pageable);

    @RestResource(path = "AllbyName")
    Page<LettreOrientationType> findByNomLettreOrientationContainingIgnoreCase(String nomLettreOrientation, Pageable pageable);

    @Override
    Optional<LettreOrientationType> findById(Long aLong);

    @PreAuthorize("hasPermission(#id, 'LettreOrientationType', 'DELETE')")
    @Override
    void deleteById(Long id);
}
