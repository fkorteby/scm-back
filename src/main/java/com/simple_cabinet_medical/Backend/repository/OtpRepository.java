package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {

   // Optional<Otp> findOtpByClientId(Long clientId);
    Optional<Otp> findOtpByEmail(String email);

    Optional<Otp> findOtpByAnnonceurId(Long id);

  //  void deleteAllByClientId(Long clientId);
    void deleteAllByEmail(String email);

    void deleteAllByAnnonceurId(Long annonceurId);

}