package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Utilisateur;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface UtilisateurRepository extends CrudRepository<Utilisateur, Long> {

    @Override
    Utilisateur save(Utilisateur utilisateur);


    Optional<Utilisateur> findByNomUtilisateur(String nomUtilisateur);

    Optional<Utilisateur> findByNom(String nom);

    @RestResource(path = "allUtilisateurs", rel = "allUtilisateurs")
    @Query("SELECT u FROM Utilisateur u")
    List<Utilisateur> findAllUtilisateurs();

    @Override
//    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN')")
    void deleteById(Long id);

}
