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
@PostFilter("hasPermission(filterObject,'READ')")
public interface ClientConfigRepository extends JpaRepository<ClientConfig, Long> {

    @PreAuthorize("((#clientConfig.idClientConfig == null or #clientConfig.idClientConfig == 0) " +
            "? hasPermission(#clientConfig,'WRITE') : hasPermission(#clientConfig,'UPDATE'))")
    ClientConfig save(ClientConfig clientConfig);

    @RestResource(path = "by-client", rel = "by-client")
    Optional<ClientConfig> findClientConfigByClientIdClient(@Param("id") Long id);

    @PreAuthorize("hasPermission(#id, 'ClientConfig', 'DELETE')")
    @Override
    void deleteById(Long id);
}
