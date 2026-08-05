package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Paraclinique;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Collection;
import java.util.Optional;

@RepositoryRestResource
@PostFilter("hasPermission(filterObject,'READ')")
public interface ParacliniqueRepository extends JpaRepository<Paraclinique, Long> {

    @PreAuthorize("((#paraclinique.idParaclinique == null or #paraclinique.idParaclinique == 0) ? hasPermission(#paraclinique,'WRITE') : hasPermission(#paraclinique,'UPDATE'))")
    Paraclinique save(Paraclinique paraclinique);

    @RestResource(path = "byClient")
    Page<Paraclinique> findAllByClientCreatorIdIn(@Param("clientId") Collection<Long> clientCreatorIds, Pageable pageable);

    @RestResource(path = "allByExamen")
    Page<Paraclinique> findByExamenContainingIgnoreCase(String examen, Pageable pageable);

    @RestResource(path = "byExamen")
    Page<Paraclinique> findByClientCreatorIdInAndExamenContainingIgnoreCase(@Param("clientId") Collection<Long> clientCreatorIds, String examen, Pageable pageable);

    @Override
    Optional<Paraclinique> findById(Long aLong);

    @PreAuthorize("hasPermission(#id, 'Paraclinique', 'DELETE')")
    @Override
    void deleteById(Long id);

    @RestResource(path = "byClientAndType")
    Page<Paraclinique> findByClientCreatorIdInAndTypeIgnoreCase(
            @Param("clientId") Collection<Long> clientCreatorIds,
            @Param("type") String type,
            Pageable pageable
    );
    @RestResource(path = "byClientAndTypeAndExamen")
    Page<Paraclinique> findByClientCreatorIdInAndTypeIgnoreCaseAndExamenContainingIgnoreCase(
            @Param("clientId") Collection<Long> clientCreatorIds,
            @Param("type") String type,
            @Param("examen") String examen,
            Pageable pageable
    );
}
