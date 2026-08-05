package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.Dto.ClientConfig.HeaderRequest;
import com.simple_cabinet_medical.Backend.model.ClientConfig;
import com.simple_cabinet_medical.Backend.repository.ClientConfigRepository;
import org.springframework.stereotype.Service;

@Service
public class ClientConfigService {
    private final ClientConfigRepository clientConfigRepository;
    private final DefaultTemplateService defaultTemplateService;

    public ClientConfigService(ClientConfigRepository clientConfigRepository,
                               DefaultTemplateService defaultTemplateService) {
        this.clientConfigRepository = clientConfigRepository;
        this.defaultTemplateService = defaultTemplateService;
    }

    /**
     * Change le header du modèle de document pour un client
     * @param headerRequest Contient le JSON de configuration du header
     * @param clientId ID du client
     * @return ClientConfig mis à jour
     */
    public ClientConfig changeHaderOfModele(HeaderRequest headerRequest, Long clientId) {
        // Récupérer la configuration du client
        ClientConfig clientConfig = clientConfigRepository
                .findClientConfigByClientIdClient(clientId)
                .orElseThrow(() -> new RuntimeException("Configuration client non trouvée"));

        // Sauvegarder le JSON de configuration du header
        clientConfig.setHeaderConfigJson(headerRequest.getHeaderContent());

        // Régénérer le template HTML complet à partir du JSON
        String fullHtmlTemplate = defaultTemplateService
                .regenerateTemplateFromJson(headerRequest.getHeaderContent());
        clientConfig.setHtmlContent(fullHtmlTemplate);

        // Sauvegarder et retourner
        return clientConfigRepository.save(clientConfig);
    }

//    /**
//     * Réinitialise le header aux valeurs par défaut du client
//     * @param clientId ID du client
//     * @return ClientConfig réinitialisé
//     */
//    public ClientConfig resetHeaderToDefault(Long clientId) {
//        ClientConfig clientConfig = clientConfigRepository
//                .findClientConfigByClientIdClient(clientId)
//                .orElseThrow(() -> new RuntimeException("Configuration client non trouvée"));
//
//        // Régénérer le header par défaut à partir des infos du client
//        String defaultHeaderJson = defaultTemplateService
//                .generateDefaultHeaderJson(clientConfig.getClient());
//        String defaultTemplate = defaultTemplateService
//                .generateTemplateForClient(clientConfig.getClient());
//
//        clientConfig.setHeaderConfigJson(defaultHeaderJson);
//        clientConfig.setHtmlContent(defaultTemplate);
//
//        return clientConfigRepository.save(clientConfig);
//    }
}