package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Traitement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

@RepositoryRestResource
@PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT')")
@PostFilter("hasPermission(filterObject,'READ')")
public interface TraitementRepository extends JpaRepository<Traitement, Long> {

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT') and (#traitement.idTraitement == null ? hasPermission(#traitement,'WRITE') : hasPermission(#traitement,'UPDATE'))")
    Traitement save(Traitement traitement);


    @Override
    @PreAuthorize("hasPermission(#id,'Traitement','DELETE') and hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT')")
    void deleteById(Long id);
}

