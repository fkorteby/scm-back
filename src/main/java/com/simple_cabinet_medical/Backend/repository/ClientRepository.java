package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

@RepositoryRestResource
@PreAuthorize("hasAnyAuthority('ADMIN')")
public interface ClientRepository extends JpaRepository<Client, Long> {

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MEDECIN', 'RREMPLACANT', 'SECRETAIRE')")
    @PostFilter("hasPermission(filterObject,'READ')")
    Optional<Client> findById(Long aLong);
}