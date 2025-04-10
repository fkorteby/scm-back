package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

@RepositoryRestResource
@Transactional
@PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
@PostFilter("hasPermission(filterObject,'READ')")
public interface PatientRepository extends JpaRepository<Patient, Long> {
}
