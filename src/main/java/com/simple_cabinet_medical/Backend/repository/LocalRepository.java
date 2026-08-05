package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Local;
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
public interface LocalRepository extends JpaRepository<Local, Long> {

   // @PreAuthorize("((#local.idLocal == null or #local.idLocal == 0) ? hasPermission(#local,'WRITE') : hasPermission(#local,'UPDATE'))")
    Local save(Local local);

    @RestResource(path = "by-client", rel = "by-client")
    Page<Local> findByClientCreatorId(@Param("clientId") Long clientId, Pageable pageable);

    @PreAuthorize("hasPermission(#id, 'Local', 'DELETE')")
    @Override
    void deleteById(Long id);
}
