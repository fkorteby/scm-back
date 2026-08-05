package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Posologie;
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
public interface PosologieRepository extends JpaRepository<Posologie, Long> {

    @PreAuthorize("((#posologie.idPosologie == null or #posologie.idPosologie == 0)? hasPermission(#posologie,'WRITE') : hasPermission(#posologie,'UPDATE'))")
    Posologie save(Posologie posologie);

    @RestResource(path = "byClient")
    Page<Posologie> findAllByClientCreatorIdIn(@Param("clientId") Collection<Long> clientCreatorIds, Pageable pageable);

    @RestResource(path = "byPosologie")
    Page<Posologie> findByClientCreatorIdInAndPosologieContainingIgnoreCase(@Param("clientId") Collection<Long> clientCreatorIds,
                                                                            String posologie, Pageable pageable);

    @RestResource(path = "allByPosologie")
    Page<Posologie> findByPosologieContainingIgnoreCase(String posologie, Pageable pageable);

    @Query("SELECT p FROM Posologie p " +
            "WHERE LOWER(REPLACE(p.posologie, ' ', '')) LIKE LOWER(REPLACE(:posologie, ' ', ''))")
    Optional<Posologie> findByPosologieIgnoreSpacesAndCase(String posologie);

    @Override
    Optional<Posologie> findById(Long aLong);

    @PreAuthorize("hasPermission(#id, 'Posologie', 'DELETE')")
    @Override
    void deleteById(Long id);
}
