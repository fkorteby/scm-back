package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Medicament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
@PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN')")
@PostFilter("hasPermission(filterObject,'READ')")
public interface MedicamentRepository extends JpaRepository<Medicament, Long> {

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN', 'REMPLACANT')")
    Optional<Medicament> findById(Long id);

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN', 'REMPLACANT')")
    List<Medicament> findAll();

}
