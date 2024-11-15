package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Client;
import com.simple_cabinet_medical.Backend.model.Conduite;
import com.simple_cabinet_medical.Backend.model.Paraclinique;
import com.simple_cabinet_medical.Backend.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends CrudRepository<Patient, Long> {

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN') or hasAuthority('REMPLACANT')")
    Patient save (Patient patient);

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN') or hasAuthority('REMPLACANT')")
    Optional<Patient> findById (Long id);

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN') or hasAuthority('REMPLACANT')")
    Optional<Patient> findByNom (String nom);

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN') or hasAuthority('REMPLACANT')")

    List<Patient> findAll ();

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN') or hasAuthority('REMPLACANT')")
    void deleteById (Long id);

}
