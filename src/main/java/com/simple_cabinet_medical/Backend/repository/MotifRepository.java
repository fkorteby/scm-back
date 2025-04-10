package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Motif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
@PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN')")
@PostFilter("hasPermission(filterObject,'READ')")
public interface MotifRepository extends JpaRepository<Motif, Long> {

    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT')")
    Optional<Motif> findByMotif(String motif);

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT')")
    List<Motif> findAll();

}
