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
@PostFilter("hasPermission(filterObject,'READ')")
public interface TraitementRepository extends JpaRepository<Traitement, Long> {

    @PreAuthorize("((#traitement.idTraitement == null or #traitement.idTraitement == 0) ? hasPermission(#traitement,'WRITE') : hasPermission(#traitement,'UPDATE'))")
    Traitement save(Traitement traitement);

    @RestResource(path = "by-client", rel = "by-client")
    Page<Traitement> findByClientCreatorId(@Param("clientId") Long clientId, Pageable pageable);

    @PreAuthorize("hasPermission(#id,'Traitement','DELETE')")
    void deleteById(Long id);
}

