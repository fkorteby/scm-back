package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Traitement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

@RepositoryRestResource
@PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT')")
@PostFilter("hasPermission(filterObject,'READ')")
public interface TraitementRepository extends JpaRepository<Traitement, Long> {

    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT') and (#traitement.idTraitement == null ? hasPermission(#traitement,'WRITE') : hasPermission(#traitement,'UPDATE'))")
    Traitement save(Traitement traitement);

    @RestResource(path = "by-client", rel = "by-client")
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Page<Traitement> findByClientCreatorId(@Param("clientId") Long clientId, Pageable pageable);

    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT') and hasPermission(#id,'Consultation','DELETE')")
    void deleteById(Long id);
}

