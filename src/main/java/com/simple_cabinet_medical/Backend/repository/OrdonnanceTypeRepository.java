package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.Projection.OrdonnanceTypeProjection;
import com.simple_cabinet_medical.Backend.model.OrdonnanceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

@RepositoryRestResource(excerptProjection = OrdonnanceTypeProjection.class)
@PostFilter("hasPermission(filterObject,'READ')")
public interface OrdonnanceTypeRepository extends JpaRepository<OrdonnanceType, Long> {

    @PreAuthorize("((#ordonnanceType.idOrdonnanceType == null or #ordonnanceType.idOrdonnanceType == 0) ? hasPermission(#ordonnanceType,'WRITE') : hasPermission(#ordonnanceType,'UPDATE'))")
    OrdonnanceType save(OrdonnanceType ordonnanceType);
    @RestResource(path = "byClient")
    Page<OrdonnanceType> findAllByClientCreatorId(@Param("clientId") Long clientCreatorId, Pageable pageable);

    @RestResource(path = "byName")
    Page<OrdonnanceType> findByClientCreatorIdAndNameContainingIgnoreCase(@Param("clientId") Long clientCreatorId, String name, Pageable pageable);

    @RestResource(path = "AllbyName")
    Page<OrdonnanceType> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Override
    Optional<OrdonnanceType> findById(Long aLong);

    @PreAuthorize("hasPermission(#id, 'OrdonnanceType', 'DELETE')")
    @Override
    void deleteById(Long id);
}
