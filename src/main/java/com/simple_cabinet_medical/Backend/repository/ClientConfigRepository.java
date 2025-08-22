package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.ClientConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

@RepositoryRestResource
@PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN')")
@PostFilter("hasPermission(filterObject,'READ')")
public interface ClientConfigRepository extends JpaRepository<ClientConfig, Long> {

    @RestResource(path = "by-client", rel = "by-client")
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Optional<ClientConfig> findClientConfigByClientIdClient(@Param("id") Long id);
}
