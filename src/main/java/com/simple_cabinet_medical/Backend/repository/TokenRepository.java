package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, String> {
}
