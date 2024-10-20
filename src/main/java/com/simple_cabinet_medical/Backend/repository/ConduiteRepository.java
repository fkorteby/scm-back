package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Client;
import com.simple_cabinet_medical.Backend.model.Conduite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConduiteRepository extends CrudRepository<Conduite, Long> {

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN')")
    Conduite save (Conduite conduite);

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN')")
    void deleteById (Long id);

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN')")
    void deleteByConduite (String conduite);

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN')")
    Optional<Conduite> findById (Long id);

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN')")
    Optional<Conduite> findByConduite (String conduite);

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN') or hasAuthority('REMPLACANT')")
    List<Conduite> findAll ();

}
