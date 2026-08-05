package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.Projection.MedicamentsProjection;
import com.simple_cabinet_medical.Backend.model.Medicament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource(excerptProjection = MedicamentsProjection.class)
@PostFilter("hasPermission(filterObject,'READ')")
public interface MedicamentRepository extends JpaRepository<Medicament, Long> {

    @PreAuthorize("hasPermission(#medicament,'WRITE') or hasPermission(#medicament,'UPDATE')")
    @Override
    Medicament save(Medicament medicament);

    @RestResource(path = "byClient")
    Page<Medicament> findAllByClientCreatorIdIn(@Param("clientId") Collection<Long> clientCreatorIds, Pageable pageable);

    @RestResource(path = "byMedicament")
    Page<Medicament> findByClientCreatorIdInAndNomCommercialeContainingIgnoreCase(
            @Param("clientId") Collection<Long> clientCreatorIds, String nomCommerciale, Pageable pageable);

    @RestResource(path = "allByMedicament")
    Page<Medicament> findByNomCommercialeContainingIgnoreCase(String nomCommerciale, Pageable pageable);

    @Override
    Optional<Medicament> findById(Long aLong);

    @PreAuthorize("hasPermission(#id, 'Medicament', 'DELETE')")
    @Override
    void deleteById(Long id);

    List<Medicament> findByNomCommercialeIgnoreCase(String nomCommerciale);

    /**
     * Recherche par nom ET forme galénique.
     * Permet de distinguer "DOLIPRANE cp." de "DOLIPRANE supp.".
     */
    List<Medicament> findByNomCommercialeIgnoreCaseAndFormeContainingIgnoreCase(
            String nomCommerciale, String forme);

    /**
     * Recherche par nom ET dosage.
     * Utile si même médicament avec dosages différents.
     */
    List<Medicament> findByNomCommercialeIgnoreCaseAndDosageIgnoreCase(
            String nomCommerciale, String dosage);

    List<Medicament> findMedicamentsByClientCreatorId(Long defaultClientId);
}
