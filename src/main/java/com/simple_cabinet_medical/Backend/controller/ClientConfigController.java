package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.Dto.ClientConfig.HeaderRequest;
import com.simple_cabinet_medical.Backend.model.ClientConfig;
import com.simple_cabinet_medical.Backend.service.ClientConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clientConfigs")
public class ClientConfigController {
    private final ClientConfigService clientConfigService;

    public ClientConfigController(ClientConfigService clientConfigService) {
        this.clientConfigService = clientConfigService;
    }
    @PatchMapping("/header/{id}")
    @PreAuthorize("@authz.canChangeClientProprite(#id)")
    public ResponseEntity<ClientConfig> changeHeaderOfModele(@RequestBody HeaderRequest headerRequest, @PathVariable Long id) {
        ClientConfig clientConfig = clientConfigService.changeHaderOfModele(headerRequest, id);
        return new ResponseEntity<>(clientConfig, HttpStatus.OK);
    }
}
