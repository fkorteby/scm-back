package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepo extends JpaRepository<Person, Long> {

    Person findPersonById (Long id);
}
