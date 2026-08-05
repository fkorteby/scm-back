//package com.simple_cabinet_medical.Backend.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.simple_cabinet_medical.Backend.Dto.ClientDto;
//import com.simple_cabinet_medical.Backend.Dto.ClientRegister;
//import com.simple_cabinet_medical.Backend.Dto.OtpVerificationRequest;
//import com.simple_cabinet_medical.Backend.Permission.UtilisateurPermision;
//import com.simple_cabinet_medical.Backend.model.EStatus;
//import com.simple_cabinet_medical.Backend.service.AuthenticationService;
//import com.simple_cabinet_medical.Backend.service.ClientService;
//import com.simple_cabinet_medical.Backend.service.JwtService;
//import com.simple_cabinet_medical.Backend.service.OTPSerivce;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.AuditorAware;
//import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(ClientController.class)
//class ClientControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private ClientService clientService;
//
//    @MockBean
//    private OTPSerivce otpSerivce;
//
//    @MockBean(name = "authz")
//    private UtilisateurPermision authz;
//
//    @Autowired
//    private ObjectMapper objectMapper;
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
////    @Test
////    @WithMockUser(authorities = "ADMIN")
////    void shouldChangeClientStatus() throws Exception {
////        Long clientId = 1L;
////
////        ClientDto clientDto = new ClientDto();
////        clientDto.setIdClient(clientId);
////        clientDto.setNomClient("Client A");
////        clientDto.setStatus(EStatus.ACTIVE);
////
////        Mockito.when(clientService.changeStatus(clientId)).thenReturn(clientDto);
////
////        mockMvc.perform(put("/api/v1/clients/status/{id}", clientId)
////                        .with(csrf()))
////                .andExpect(status().isCreated())
////                .andExpect(jsonPath("$.idClient").value(clientId))
////                .andExpect(jsonPath("$.nomClient").value("Client A"))
////                .andExpect(jsonPath("$.status").value("ACTIVE"));
////    }
////
////    @Test
////    @WithMockUser(authorities = "USER")
////    void shouldReturnForbiddenWhenNonAdminChangesStatus() throws Exception {
////        mockMvc.perform(put("/api/v1/clients/status/{id}", 1L)
////                        .with(csrf()))
////                .andExpect(status().isForbidden());
////    }
////
////    @Test
////    void shouldRegisterClientWithLogo() throws Exception {
////        ClientRegister request = new ClientRegister();
////        request.setNomClient("Client B");
////        request.setEmail("clientb@mail.com");
////
////        MockMultipartFile clientPart = new MockMultipartFile(
////                "client",
////                "",
////                "application/json",
////                objectMapper.writeValueAsBytes(request)
////        );
////
////        MockMultipartFile logoPart = new MockMultipartFile(
////                "imageLogo",
////                "logo.png",
////                "image/png",
////                "fake-image-content".getBytes()
////        );
////
////        ClientDto responseDto = new ClientDto();
////        responseDto.setIdClient(2L);
////        responseDto.setNomClient("Client B");
////
////        Mockito.when(clientService.clientRegister(any(ClientRegister.class), any()))
////                .thenReturn(responseDto);
////
////        mockMvc.perform(multipart("/api/v1/clients/register")
////                        .file(clientPart)
////                        .file(logoPart)
////                        .with(csrf()))
////                .andExpect(status().isCreated())
////                .andExpect(jsonPath("$.idClient").value(2L))
////                .andExpect(jsonPath("$.nomClient").value("Client B"));
////    }
////
////
////    @Test
////    @WithMockUser
////    void shouldChangeClientLogo() throws Exception {
////        Long clientId = 3L;
////
////        MockMultipartFile logoPart = new MockMultipartFile(
////                "imageLogo",
////                "logo.png",
////                "image/png",
////                "new-logo-content".getBytes()
////        );
////
////        ClientDto clientDto = new ClientDto();
////        clientDto.setIdClient(clientId);
////        clientDto.setNomClient("Client C");
////
////        Mockito.when(authz.canChangeClientProprite(clientId)).thenReturn(true);
////        Mockito.when(clientService.changeLogo(eq(clientId), any())).thenReturn(clientDto);
////
////        mockMvc.perform(multipart("/api/v1/clients/changeLogo/{id}", clientId)
////                        .file(logoPart)
////                        .with(csrf())
////                        .with(request -> { request.setMethod("PATCH"); return request; })
////                )
////                .andExpect(status().isOk())
////                .andExpect(jsonPath("$.idClient").value(clientId))
////                .andExpect(jsonPath("$.nomClient").value("Client C"));
////    }
////
////    @Test
////    @WithMockUser
////    void shouldReturnForbiddenWhenChangeLogoNotAllowed() throws Exception {
////        Long clientId = 4L;
////
////        MockMultipartFile logoPart = new MockMultipartFile(
////                "imageLogo",
////                "logo.png",
////                "image/png",
////                "logo-content".getBytes()
////        );
////
////        Mockito.when(authz.canChangeClientProprite(clientId)).thenReturn(false);
////
////        mockMvc.perform(multipart("/api/v1/clients/changeLogo/{id}", clientId)
////                        .file(logoPart)
////                        .with(csrf())
////                        .with(request -> { request.setMethod("PATCH"); return request; })
////                )
////                .andExpect(status().isForbidden());
////    }
////
////
////    @Test
////    void shouldVerifyClientOtp() throws Exception {
////        OtpVerificationRequest request = new OtpVerificationRequest();
////        request.setId(5L);
////        request.setOtp("123456");
////
////        ClientDto clientDto = new ClientDto();
////        clientDto.setIdClient(5L);
////        clientDto.setNomClient("Client D");
////
////        Mockito.when(otpSerivce.verifyOtpForClient(any(OtpVerificationRequest.class))).thenReturn(clientDto);
////
////        mockMvc.perform(post("/api/v1/clients/checkClientOtp")
////                        .with(csrf())
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(objectMapper.writeValueAsString(request)))
////                .andExpect(status().isOk())
////                .andExpect(jsonPath("$.idClient").value(5L))
////                .andExpect(jsonPath("$.nomClient").value("Client D"));
////    }
////
////    @Test
////    void shouldRegenerateClientOtp() throws Exception {
////        Long clientId = 6L;
////
////        ClientDto clientDto = new ClientDto();
////        clientDto.setIdClient(clientId);
////        clientDto.setNomClient("Client E");
////
////        Mockito.when(clientService.regenerateOtp(clientId)).thenReturn(clientDto);
////
////        mockMvc.perform(post("/api/v1/clients/regenerateOtp")
////                        .with(csrf())
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(objectMapper.writeValueAsString(clientId)))
////                .andExpect(status().isOk())
////                .andExpect(jsonPath("$.idClient").value(clientId))
////                .andExpect(jsonPath("$.nomClient").value("Client E"));
////    }
//}
