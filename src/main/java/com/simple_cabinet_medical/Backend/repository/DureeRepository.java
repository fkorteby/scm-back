package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Duree;
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
public interface DureeRepository extends JpaRepository<Duree, Long> {

    @PreAuthorize("((#duree.idDuree == null or #duree.idDuree == 0 ) ? hasPermission(#duree,'WRITE') : hasPermission(#duree,'UPDATE'))")
    Duree save(Duree duree);

    @RestResource(path = "byClient")
    Page<Duree> findAllByClientCreatorIdIn(@Param("clientId") Collection<Long> clientCreatorIds, Pageable pageable);

    @RestResource(path = "byDuree")
    Page<Duree> findByClientCreatorIdInAndDureeContainingIgnoreCase(@Param("clientId") Collection<Long> clientCreatorIds,
                                                                    String duree, Pageable pageable);

    @RestResource(path = "allByDuree")
    Page<Duree> findByDureeContainingIgnoreCase(String duree, Pageable pageable);

    @Query("SELECT d FROM Duree d " +
            "WHERE LOWER(REPLACE(d.duree, ' ', '')) LIKE LOWER(REPLACE(:duree, ' ', ''))")
    Optional<Duree> findByDureeIgnoreSpacesAndCase(String duree);

    @Override
    Optional<Duree> findById(Long aLong);

    @PreAuthorize("hasPermission(#id, 'Duree', 'DELETE')")
    @Override
    void deleteById(Long id);
}
