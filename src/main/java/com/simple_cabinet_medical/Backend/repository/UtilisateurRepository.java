package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Client;
import com.simple_cabinet_medical.Backend.model.Conduite;
import com.simple_cabinet_medical.Backend.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends CrudRepository<Utilisateur, Long> {

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    Utilisateur save (Utilisateur utilisateur);

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    void deleteById (Long id);

    @PreAuthorize("hasAuthority('ADMIN')")
    Optional<Utilisateur> findByNomUtilisateur (String nomUtilisateur);

    @PreAuthorize("hasAuthority('ADMIN')")
    Optional<Utilisateur> findByNom (String nom);

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    List<Utilisateur> findAll ();
}
