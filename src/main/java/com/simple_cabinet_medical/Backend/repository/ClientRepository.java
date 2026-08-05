package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.Projection.ClientProjection;
import com.simple_cabinet_medical.Backend.model.Client;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource(excerptProjection = ClientProjection.class)
//@PreAuthorize("hasAnyAuthority('ADMIN')")
//@PostFilter("hasPermission(filterObject,'READ')")
public interface ClientRepository extends JpaRepository<Client, Long> {

    //@PreAuthorize("(#client.idClient == null ? hasPermission(#client,'WRITE') : hasPermission(#client,'UPDATE'))")
    Client save(Client client);

    boolean existsByEmail(String email);
    boolean existsByTelephone(String telephone);
    boolean existsByNomClient(String nomClient);
    boolean existsByNomClientEnArabe(String nomClient);
    boolean existsByAdresse(String add);

//    @Override
//    @PostFilter("hasPermission(filterObject,'READ')")
//    @PreAuthorize("hasAnyAuthority('ADMIN', 'MEDECIN', 'REMPLACANT', 'SECRETAIRE','MEDECIN_PRINCIPAL')")
    Optional<Client> findById(Long aLong);

    Optional<Client> findByEmail(@NotBlank String email);

    @Override
    void deleteById(Long id);

    @Override
    @PostFilter("hasPermission(filterObject,'READ')")
    Page<Client> findAll(Pageable pageable);

    @PostFilter("hasPermission(filterObject,'READ')")
    @RestResource(path = "by-nom", rel = "by-nom")
    Page<Client> findAllByNomClientContainingIgnoreCase(@NotBlank String nomClient, Pageable pageable);


    // Admin dash ----------------------

    long countByIdClientNot(Long id);

    long countByDateCreationAfterAndIdClientNot(Date startDate, Long id);

    @Query("SELECT COUNT(DISTINCT c) FROM Client c JOIN c.consultations con " +
            "WHERE con.dateConsultation >= :startDate AND c.idClient <> :excludedId")
    long countActiveDoctorsSince(@Param("startDate") LocalDate startDate, @Param("excludedId") Long excludedId);

    @Query("SELECT FUNCTION('MONTH', c.dateCreation), COUNT(c) " +
            "FROM Client c " +
            "WHERE FUNCTION('YEAR', c.dateCreation) = :year AND c.idClient <> :excludedId " +
            "GROUP BY FUNCTION('MONTH', c.dateCreation) " +
            "ORDER BY FUNCTION('MONTH', c.dateCreation) ASC")
    List<Object[]> countDoctorsPerMonthByYear(@Param("year") int year, @Param("excludedId") Long excludedId);

    @Query("SELECT c.dateCreation FROM Client c WHERE c.dateCreation IS NOT NULL AND c.idClient <> :excludedId")
    List<Date> findAllCreationDates(@Param("excludedId") Long excludedId);

    @Query("SELECT c.ville, c.pays, COUNT(c), SUM(SIZE(c.locals)) " +
            "FROM Client c " +
            "WHERE c.ville IS NOT NULL AND c.pays IS NOT NULL AND c.idClient <> :excludedId " +
            "GROUP BY c.ville, c.pays " +
            "ORDER BY COUNT(c) DESC")
    List<Object[]> getGeoStatistics(@Param("excludedId") Long excludedId);
}