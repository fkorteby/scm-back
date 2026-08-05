//package com.simple_cabinet_medical.Backend.controller;
//
//import com.simple_cabinet_medical.Backend.Dto.UtilisateurDto;
//import com.simple_cabinet_medical.Backend.model.EStatus;
//import com.simple_cabinet_medical.Backend.service.JwtService;
//import com.simple_cabinet_medical.Backend.service.UtilisateurService;
//import com.fasterxml.jackson.databind.ObjectMapper;
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
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(UtilisateurController.class)
//@WithMockUser(username = "admin", roles = {"ADMIN"})
//class UtilisateurControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private UtilisateurService utilisateurService;
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
//    @Test
//    void shouldChangeStatusSuccessfully() throws Exception {
//        // GIVEN
//        Long userId = 1L;
//
//        UtilisateurDto utilisateurDto = new UtilisateurDto();
//        utilisateurDto.setIdUtilisateur(userId);
//        utilisateurDto.setStatus(EStatus.ACTIVE);
//
//        Mockito.when(utilisateurService.changeStatus(userId))
//                .thenReturn(utilisateurDto);
//
//        // WHEN + THEN
//        mockMvc.perform(put("/api/v1/utilisateurs/status/{id}", userId)
//                        .with(csrf()) // Required for PUT/POST requests when security is enabled
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.idUtilisateur").value(userId))
//                .andExpect(jsonPath("$.status").value("ACTIVE"));
//    }
//}