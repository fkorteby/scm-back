package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Forme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
@PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN')")
@PostFilter("hasPermission(filterObject,'READ')")
public interface FormeRepository extends JpaRepository<Forme, Long> {

    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN', 'REMPLACANT')")
    Optional<Forme> findByForme(String forme);

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN', 'REMPLACANT')")
    List<Forme> findAll();
}
