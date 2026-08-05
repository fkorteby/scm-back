package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Optional;

//@RepositoryRestResource(excerptProjection = UtilisateurProjection.class)
//@PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN')")
//@PostFilter("@authz.hasCustomPermission(filterObject)")
// I IMPLMNTED THE SECURITE IN CONTROLLER LAYER
@RepositoryRestResource
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByNomUtilisateur(String nomUtilisateur);

    //    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Page<Utilisateur> findByClientIdClient(@Param("clientId") Long clientId, Pageable pageable);

    //  @PreAuthorize("hasPermission(#idUtilisateur, 'Utilisateur', 'DELETE')")
    void deleteByIdUtilisateur(Long idUtilisateur);

    boolean existsByNomUtilisateur(String username);

    Optional<Utilisateur> findUtilisateurByClientIdClient(Long clientId);

    Optional<Utilisateur> findUtilisateurByEmail(String userEmail);

    @RestResource(path = "byNom", rel = "byNom")
    @Query("""
    SELECT u
    FROM Utilisateur u
    WHERE LOWER(u.nom) LIKE LOWER(CONCAT('%', :items, '%'))
       OR LOWER(u.prenom) LIKE LOWER(CONCAT('%', :items, '%'))
""")
    Page<Utilisateur> searchByNom(
            @Param("items") String items,
            Pageable pageable);

    @RestResource(path = "byNomAndClint", rel = "byNomAndClint")
    @Query("""
    SELECT u
    FROM Utilisateur u
    WHERE u.client.idClient = :clientId
      AND (
            LOWER(u.nom) LIKE LOWER(CONCAT('%', :items, '%'))
         OR LOWER(u.prenom) LIKE LOWER(CONCAT('%', :items, '%'))
      )
""")
    Page<Utilisateur> searchByClientAndNom(
            @Param("clientId") Long clientId,
            @Param("items") String items,
            Pageable pageable);
}
