package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Forme;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Collection;
import java.util.Optional;

@RepositoryRestResource
@PostFilter("hasPermission(filterObject,'READ')")
public interface FormeRepository extends JpaRepository<Forme, Long> {

    @PreAuthorize("((#forme.idForme == null or #forme.idForme == 0 ) ? hasPermission(#forme,'WRITE') : hasPermission(#forme,'UPDATE'))")
    Forme save(Forme forme);

    @RestResource(path = "byClient")
    Page<Forme> findAllByClientCreatorIdIn(@Param("clientId") Collection<Long> clientCreatorIds, Pageable pageable);

    @RestResource(path = "byForme")
    Page<Forme> findByClientCreatorIdInAndFormeContainingIgnoreCase(@Param("clientId") Collection<Long> clientCreatorIds, @NotNull String forme, Pageable pageable);

    @RestResource(path = "allByForme")
    Page<Forme> findByFormeContainingIgnoreCase(@NotNull String forme, Pageable pageable);

    @Query("SELECT f FROM Forme f " +
            "WHERE LOWER(REPLACE(f.forme, ' ', '')) LIKE LOWER(REPLACE(:forme, ' ', ''))")
    Optional<Forme> findByFormeIgnoreSpacesAndCase(String forme);

    @Override
    Optional<Forme> findById(Long aLong);

    @PreAuthorize("hasPermission(#id, 'Forme', 'DELETE')")
    @Override
    void deleteById(Long id);
}
