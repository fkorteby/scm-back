package com.simple_cabinet_medical.Backend.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserRequest {

    private String nomUtilisateur;
    private String mdp;
    private String nom;
    private String role;
}
