package com.simple_cabinet_medical.Backend.Dto;

public record ChatUserDto(
        Long id,
        String Client,
        String email,
        String firstname,
        String lastname,
        String phoneNumber,
        String username,
        String role,
        String identifierHash
) { }