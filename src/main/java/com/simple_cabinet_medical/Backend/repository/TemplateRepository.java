package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Client;
import com.simple_cabinet_medical.Backend.model.Conduite;
import com.simple_cabinet_medical.Backend.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateRepository extends CrudRepository<Template, Long> {

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN')")
    Template save (Template template);

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN')")
    void deleteById (Long id);

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN')")
    void deleteByNom (String nom);

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN')")
    Optional<Template> findById (Long id);

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN')")
    Optional<Template> findByNom (String nom);

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDECIN') or hasAuthority('REMPLACANT')")
    List<Template> findAll ();
}
