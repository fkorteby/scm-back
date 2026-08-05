//package com.simple_cabinet_medical.Backend.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.simple_cabinet_medical.Backend.Dto.ClientConfig.HeaderRequest;
//import com.simple_cabinet_medical.Backend.Permission.UtilisateurPermision;
//import com.simple_cabinet_medical.Backend.model.ClientConfig;
//import com.simple_cabinet_medical.Backend.service.AuthenticationService;
//import com.simple_cabinet_medical.Backend.service.ClientConfigService;
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
//import static org.mockito.ArgumentMatchers.eq;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(ClientConfigController.class)
//class ClientConfigControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private ClientConfigService clientConfigService;
//
//    @MockBean(name = "authz")
//    private UtilisateurPermision authz;
//
//    @Autowired
//    private ObjectMapper objectMapper;
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
//    @WithMockUser
//    void shouldChangeHeaderSuccessfully() throws Exception {
//
//        Long clientId = 1L;
//        Long clientConfigId = 1L;
//
//        // HeaderRequest JSON
//        HeaderRequest headerRequest = new HeaderRequest();
//        headerRequest.setHeaderContent("<h1>New Header Content</h1>");
//
//        // Mocked ClientConfig
//        ClientConfig clientConfig = new ClientConfig();
//        clientConfig.setIdClientConfig(clientConfigId);
//        clientConfig.setHeaderConfigJson("<h1>New Header Content</h1>");
//
//        // Stub service
//        Mockito.when(clientConfigService.changeHaderOfModele(Mockito.any(), Mockito.eq(clientId)))
//                .thenReturn(clientConfig);
//
//        Mockito.when(authz.canChangeClientProprite(clientId)).thenReturn(true);
//
//        mockMvc.perform(patch("/api/v1/clientConfigs/header/{id}", clientId)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(headerRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.idClientConfig").value(clientConfigId))
//                .andExpect(jsonPath("$.headerConfigJson").value("<h1>New Header Content</h1>"));
//    }
//}
