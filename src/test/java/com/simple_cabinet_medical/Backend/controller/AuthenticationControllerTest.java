//package com.simple_cabinet_medical.Backend.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.simple_cabinet_medical.Backend.Dto.UtilisateurDto;
//import com.simple_cabinet_medical.Backend.model.EROLE;
//import com.simple_cabinet_medical.Backend.model.EStatus;
//import com.simple_cabinet_medical.Backend.payload.request.LoginRequest;
//import com.simple_cabinet_medical.Backend.payload.request.RegisterUserRequest;
//import com.simple_cabinet_medical.Backend.payload.response.LoginResponse;
//import com.simple_cabinet_medical.Backend.service.AuthenticationService;
//import com.simple_cabinet_medical.Backend.service.JwtService;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.AuditorAware;
//import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Map;
//
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(AuthenticationController.class)
//class AuthenticationControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private AuthenticationService authenticationService;
//
//    @MockBean
//    private JwtService jwtService;
//
//    @MockBean
//    private UserDetailsService userDetailsService;
//
//    @MockBean
//    private JpaMetamodelMappingContext jpaMappingContext;
//
//    @MockBean(name = "auditorAware")
//    private AuditorAware<String> auditorAware;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//
//    @Test
//    @WithMockUser(authorities = {"ADMIN"})
//    void shouldRegisterUserSuccessfully() throws Exception {
//
//        RegisterUserRequest request = new RegisterUserRequest(
//                "aymen.messiikh@mail.com",
//                "MESSIKH",
//                "Aymen",
//                EROLE.ADMIN,
//                1L
//        );
//
//        UtilisateurDto response = new UtilisateurDto(
//                1L,
//                "MESSIKH",
//                "Aymen",
//                "aymen.messiikh@mail.com",
//                "messikh.aymen",
//                EROLE.ADMIN,
//                "Client A",
//                EStatus.ACTIVE
//        );
//
//        Mockito.when(authenticationService.signup(Mockito.any()))
//                .thenReturn(response);
//
//        mockMvc.perform(post("/api/v1/auth/signup")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.email").value("aymen.messiikh@mail.com"));
//    }
//
//
//    @Test
//    @WithMockUser(authorities = {"USER"})
//    void shouldReturn403WhenSignupUnauthorized() throws Exception {
//
//        mockMvc.perform(post("/api/v1/auth/signup")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser
//    void shouldAuthenticateUser() throws Exception {
//
//        LoginRequest request = new LoginRequest(
//                "messikh.aymen",
//                "password123",
//                "recaptcha-token"
//        );
//
//        LoginResponse response = new LoginResponse(
//                1L,
//                "messikh.aymen",
//                "ADMIN",
//                "jwt-token",
//                10L,
//                10L
//        );
//
//        Mockito.when(authenticationService.login(Mockito.any()))
//                .thenReturn(response);
//
//        mockMvc.perform(post("/api/v1/auth/login")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.token").value("jwt-token"));
//    }
//
//
//
//    @Test
//    @WithMockUser
//    void shouldReturnCurrentUser() throws Exception {
//
//        UtilisateurDto dto = new UtilisateurDto(
//                2L,
//                "Messikh",
//                "Aymen",
//                "aymen.messiikh@mail.com",
//                "messikh.aymen",
//                EROLE.MEDECIN,
//                "Client B",
//                EStatus.ACTIVE
//        );
//
//        Mockito.when(authenticationService.getCurrentUser())
//                .thenReturn(dto);
//
//        mockMvc.perform(get("/api/v1/auth/currentUser"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.email").value("aymen.messiikh@mail.com"))
//                .andExpect(jsonPath("$.nomUtilisateur").value("messikh.aymen"));
//    }
//
//
//    @Test
//    @WithMockUser
//    void shouldChangePasswordSuccessfully() throws Exception {
//
//        Map<String, String> request = Map.of(
//                "currentPassword", "oldPassword",
//                "newPassword", "newPassword"
//        );
//
//        Mockito.doNothing()
//                .when(authenticationService)
//                .changePassword("oldPassword", "newPassword");
//
//        mockMvc.perform(patch("/api/v1/auth/change-password")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk());
//    }
//
//}
