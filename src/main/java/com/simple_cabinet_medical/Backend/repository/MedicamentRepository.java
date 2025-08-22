package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.Projection.MedicamentsProjection;
import com.simple_cabinet_medical.Backend.model.Medicament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

@RepositoryRestResource(excerptProjection = MedicamentsProjection.class)
@PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN')")
@PostFilter("hasPermission(filterObject,'READ')")
public interface MedicamentRepository extends JpaRepository<Medicament, Long> {

    @RestResource(path = "byClient")
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Page<Medicament> findAllByClientCreatorId(@Param("clientId") Long clientCreatorId, Pageable pageable);

    @RestResource(path = "byMedicament")
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Page<Medicament> findByClientCreatorIdAndNomCommercialeContainingIgnoreCase(
            @Param("clientId") Long clientCreatorId, String nomCommerciale, Pageable pageable);

    @RestResource(path = "allByMedicament")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Page<Medicament> findByNomCommercialeContainingIgnoreCase(String nomCommerciale, Pageable pageable);

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Optional<Medicament> findById(Long aLong);
}
