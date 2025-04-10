package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

@RepositoryRestResource()
@PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN')")
@PostFilter("hasPermission(filterObject,'READ')")
public interface ClientRepository extends JpaRepository<Client, Long> {

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN') and (#client.idClient == null ? hasPermission(#client,'WRITE') : hasPermission(#client,'UPDATE'))")
    Client save(Client client);

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    void deleteById(Long id);
}
