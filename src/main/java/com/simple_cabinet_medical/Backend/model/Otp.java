package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

  //  private Long clientId;
    private String email;

    private Long userId;

    private Long annonceurId;

    private String otpHash;

    private int attempts = 0;

    @Enumerated(EnumType.STRING)
    private EOtpStatus status = EOtpStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime expiresAt;

    private String purpose;

    public Otp() {
    }

    public Otp(Long id, String email, Long userId, Long annonceurId, String otpHash, int attempts, EOtpStatus status, LocalDateTime createdAt, LocalDateTime expiresAt, String purpose) {
        this.id = id;
        this.email = email;
        this.userId = userId;
        this.annonceurId = annonceurId;
        this.otpHash = otpHash;
        this.attempts = attempts;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.purpose = purpose;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAnnonceurId() {
        return annonceurId;
    }

    public void setAnnonceurId(Long annonceurId) {
        this.annonceurId = annonceurId;
    }

    public String getOtpHash() {
        return otpHash;
    }

    public void setOtpHash(String otpHash) {
        this.otpHash = otpHash;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public EOtpStatus getStatus() {
        return status;
    }

    public void setStatus(EOtpStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}
