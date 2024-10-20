package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends CrudRepository<Client, Long> {

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    Client save (Client client);

    @PreAuthorize("hasAuthority('ADMIN')")
    Optional<Client> findByNom (String nom);

    @PreAuthorize("hasAuthority('ADMIN')")
    Optional<Client> findById (Long id);

    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    List<Client> findAll ();

    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    void deleteById (Long id);

    @PreAuthorize("hasAuthority('ADMIN')")
    void deleteByNom (String nom);
}
