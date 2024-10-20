package com.simple_cabinet_medical.Backend.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private Long id;
    private String nomUtilisateur;
    private String role;
    private String token;

    public LoginResponse (String accessToken, Long id, String username, String role) {
        this.token = accessToken;
        this.id = id;
        this.nomUtilisateur = username;
        this.role = role;
    }
}
