package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Client;
import com.simple_cabinet_medical.Backend.model.Conduite;
import com.simple_cabinet_medical.Backend.model.Forme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormeRepository extends JpaRepository<Forme, Long> {

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN')")
    Forme save (Forme forme);

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN')")
    void deleteById (Long id);

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN')")
    void deleteByForme (String forme);

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN')")
    Optional<Forme> findById (Long id);

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN')")
    Optional<Forme> findByForme (String forme);

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN') or hasAuthority('REMPLACANT')")
    List<Forme> findAll ();

    List<Forme> findAllByIdUtilisateur (Long idUtilisateur);
}
