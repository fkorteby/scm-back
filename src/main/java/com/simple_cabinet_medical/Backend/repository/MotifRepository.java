package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Client;
import com.simple_cabinet_medical.Backend.model.Conduite;
import com.simple_cabinet_medical.Backend.model.Motif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MotifRepository extends JpaRepository<Motif, Long> {

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN')")
    Motif save (Motif motif);

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN')")
    void deleteById (Long id);

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN')")
    void deleteByMotif (String motif);

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN')")
    Optional<Motif> findById (Long id);

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN')")
    Optional<Motif> findByMotif (String motif);

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN') or hasAuthority('REMPLACANT')")
    List<Motif> findAll ();

    List<Motif> findAllByIdUtilisateur (Long id);

}
