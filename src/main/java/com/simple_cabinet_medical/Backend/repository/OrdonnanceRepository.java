package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Ordonnance;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
@PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT')")
@PostFilter("hasPermission(filterObject,'READ')")
public interface OrdonnanceRepository extends CrudRepository<Ordonnance, Long> {

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT') and (#ordonnance.idOrdonnance == null ? hasPermission(#ordonnance,'WRITE') : hasPermission(#ordonnance,'UPDATE'))")
    Ordonnance save(Ordonnance ordonnance);

    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Optional<Ordonnance> findById(Long id);

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    List<Ordonnance> findAll();

    @Override
    @PreAuthorize("hasPermission(#id,'Ordonnance','DELETE') and hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT')")
    void deleteById(Long id);
}
