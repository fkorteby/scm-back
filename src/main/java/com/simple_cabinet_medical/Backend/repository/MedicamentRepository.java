package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Client;
import com.simple_cabinet_medical.Backend.model.Conduite;
import com.simple_cabinet_medical.Backend.model.Medicament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicamentRepository extends CrudRepository<Medicament, Long> {

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN')")
    Medicament save (Medicament medicament);

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN')")
    void deleteById (Long id);

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN')")
    void deleteByNomCommerciale (String nomCommerciale);

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN')")
    Optional<Medicament> findById (Long id);

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN')")
    Optional<Medicament> findByNomCommerciale (String nomCommerciale);

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN') or hasAuthority('REMPLACANT')")
    List<Medicament> findAll ();

}
