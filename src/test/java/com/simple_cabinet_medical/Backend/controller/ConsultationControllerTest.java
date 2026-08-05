//package com.simple_cabinet_medical.Backend.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.simple_cabinet_medical.Backend.Dto.DiagnosticDTO;
//import com.simple_cabinet_medical.Backend.service.AuthenticationService;
//import com.simple_cabinet_medical.Backend.service.ConsultationService;
//import com.simple_cabinet_medical.Backend.service.JwtService;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.AuditorAware;
//import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(ConsultationController.class)
//@AutoConfigureMockMvc(addFilters = false)
//class ConsultationControllerTest {
//
////    @Autowired
////    private MockMvc mockMvc;
////
////    @MockBean
////    private ConsultationService consultationService;
////
////    @MockBean
////    private AuthenticationService authenticationService;
////
////    @MockBean
////    private JwtService jwtService;
////
////    @MockBean
////    private UserDetailsService userDetailsService;
////
////    @MockBean
////    private JpaMetamodelMappingContext jpaMappingContext;
////
////    @MockBean(name = "auditorAware")
////    private AuditorAware<String> auditorAware;
////
////    @Autowired
////    private ObjectMapper objectMapper;
////
////    @Test
////    void shouldReturnDiagnosticsSuccessfully() throws Exception {
////        // Arrange
////        DiagnosticDTO diagnostic1 = new DiagnosticDTO("Flu");
////        DiagnosticDTO diagnostic2 = new DiagnosticDTO("Cold");
////        List<DiagnosticDTO> diagnostics = List.of(diagnostic1, diagnostic2);
////
////        Mockito.when(consultationService.getDiagnostics(anyLong(), anyString()))
////                .thenReturn(diagnostics);
////
////        // Act & Assert
////        mockMvc.perform(get("/api/v1/consultations/diagnostics")
////                        .param("idClient", "1")
////                        .param("diagnosticMedical", "Flu")
////                        .contentType(MediaType.APPLICATION_JSON))
////                .andExpect(status().isOk())
////                .andExpect(jsonPath("$.length()").value(2))
////                .andExpect(jsonPath("$[0].diagnosticMedical").value("Flu"))
////                .andExpect(jsonPath("$[1].diagnosticMedical").value("Cold"));
////
////        // Verify service method was called once
////        verify(consultationService, times(1)).getDiagnostics(1L, "Flu");
////    }
////
////    @Test
////    void shouldReturnEmptyListIfNoDiagnostics() throws Exception {
////        Mockito.when(consultationService.getDiagnostics(anyLong(), anyString()))
////                .thenReturn(List.of());
////
////        mockMvc.perform(get("/api/v1/consultations/diagnostics")
////                        .param("idClient", "1")
////                        .param("diagnosticMedical", "Unknown")
////                        .contentType(MediaType.APPLICATION_JSON))
////                .andExpect(status().isOk())
////                .andExpect(jsonPath("$.length()").value(0));
////
////        verify(consultationService, times(1)).getDiagnostics(1L, "Unknown");
////    }
//}
