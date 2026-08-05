//package com.simple_cabinet_medical.Backend.controller;
//
//import com.simple_cabinet_medical.Backend.Dto.ExamenCliniqueDto;
//import com.simple_cabinet_medical.Backend.service.AuthenticationService;
//import com.simple_cabinet_medical.Backend.service.ExamenCliniqueService;
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
//import java.util.List;
//
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(ExamenCliniqueController.class)
//class ExamenCliniqueControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private ExamenCliniqueService examenCliniqueService;
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
//    @Test
//    @WithMockUser(authorities = "MEDECIN")
//    void shouldReturnExamenCliniqueTree() throws Exception {
//        ExamenCliniqueDto dto1 = new ExamenCliniqueDto();
//        ExamenCliniqueDto dto2 = new ExamenCliniqueDto();
//
//        Mockito.when(examenCliniqueService.getExamenCliniqueTree())
//                .thenReturn(List.of(dto1, dto2));
//
//        mockMvc.perform(get("/api/v1/examenCliniques/tree")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(2));
//
//        verify(examenCliniqueService, times(1))
//                .getExamenCliniqueTree();
//    }
//
//    @Test
//    void shouldReturn401WhenUnauthenticated() throws Exception {
//        mockMvc.perform(get("/api/v1/examenCliniques/tree"))
//                .andExpect(status().isUnauthorized());
//    }
//}
