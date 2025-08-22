package com.simple_cabinet_medical.Backend.payload.response;


public class LoginResponse {

    private Long id;
    private String nomUtilisateur;
    private String role;
    private String token;
    private Long client;

    public LoginResponse(Long id, String nomUtilisateur, String role, String token, Long client) {
        this.id = id;
        this.nomUtilisateur = nomUtilisateur;
        this.role = role;
        this.token = token;
        this.client = client;
    }
    public LoginResponse(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getClient() {
        return client;
    }

    public void setClient(Long client) {
        this.client = client;
    }
}