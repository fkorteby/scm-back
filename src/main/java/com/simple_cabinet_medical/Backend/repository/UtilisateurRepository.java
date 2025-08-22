package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.Projection.UtilisateurProjection;
import com.simple_cabinet_medical.Backend.model.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

@RepositoryRestResource(excerptProjection = UtilisateurProjection.class)
//@PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN')")
//@PostFilter("hasPermission(filterObject,'READ')")
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByNomUtilisateur(String nomUtilisateur);

    @RestResource(path = "by-client", rel = "by-client")
//    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Page<Utilisateur> findByClientIdClient(@Param("clientId") Long clientId, Pageable pageable);
}
