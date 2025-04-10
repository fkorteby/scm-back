package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.OptionParaclinique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RepositoryRestResource
@PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN')")
@PostFilter("hasPermission(filterObject,'READ')")
public interface OptionParacliniqueRepository extends JpaRepository<OptionParaclinique, Long> {

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT')")
    List<OptionParaclinique> findAll();
}
