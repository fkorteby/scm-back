package com.simple_cabinet_medical.Backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple_cabinet_medical.Backend.Dto.ClientDto;
import com.simple_cabinet_medical.Backend.Dto.ClientRegister;
import com.simple_cabinet_medical.Backend.Mapper.Client.ClientMapper;
import com.simple_cabinet_medical.Backend.exception.CustomBadRequestException;
import com.simple_cabinet_medical.Backend.model.*;
import com.simple_cabinet_medical.Backend.repository.ClientConfigRepository;
import com.simple_cabinet_medical.Backend.repository.ClientRepository;
import com.simple_cabinet_medical.Backend.repository.LocalRepository;
import com.simple_cabinet_medical.Backend.repository.UtilisateurRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class ClientService {

    private final String baseUrl = "https://storage.googleapis.com/scm-logos-prod";
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final FileStorageService fileStorageService;
    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurService utilisateurService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final OTPSerivce otpSerivce;
    private final DefaultTemplateService defaultTemplateService;
    private final LocalRepository localRepository;
    private final GoogleStorageService gcsService;
    private final ObjectMapper objectMapper;
    private final ClientConfigRepository clientConfigRepository;

    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper, FileStorageService fileStorageService, UtilisateurRepository utilisateurRepository, UtilisateurService utilisateurService, PasswordEncoder passwordEncoder, EmailService emailService, OTPSerivce otpSerivce, DefaultTemplateService defaultTemplateService, LocalRepository localRepository, GoogleStorageService gcsService, ObjectMapper objectMapper, ClientConfigRepository clientConfigRepository) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.fileStorageService = fileStorageService;
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurService = utilisateurService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.otpSerivce = otpSerivce;
        this.defaultTemplateService = defaultTemplateService;
        this.localRepository = localRepository;
        this.gcsService = gcsService;
        this.objectMapper = objectMapper;
        this.clientConfigRepository = clientConfigRepository;
    }

    public ClientDto changeStatus(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        if (client.getStatus().equals(EStatus.INACTIVE)) {
            client.setStatus(EStatus.ACTIVE);
        } else {
            client.setStatus(EStatus.INACTIVE);
        }
        Client client1 = clientRepository.save(client);
        return clientMapper.EntityToDto(client1);
    }

    public Client getClientFromClientConfig(ClientConfig clientConfig) {
        return clientConfig.getClient();
    }

    @Transactional
    public String clientRegister(ClientRegister clientReq,String contentType) {
        try {
            if (clientRepository.existsByEmail(clientReq.getEmail())) {;
                throw new IllegalArgumentException("Email déjà utilisé");
            }
            if (clientRepository.existsByTelephone(clientReq.getTelephone())) {
                throw new IllegalArgumentException("Téléphone déjà utilisé");
            }
//            if (contentType != null && !contentType.isEmpty() && !isValidImage(contentType)) {
//                throw new IllegalArgumentException("Type de fichier non autorisé");
//            }
            Client client = new Client();
            if (clientRepository.existsByNomClient(clientReq.getNomClient())) {
                throw new IllegalArgumentException("Nom de l'établissement / Clinique  déjà utilisé (FR ou AR)");
            }
            client.setNomClient(clientReq.getNomClient());
            if (clientRepository.existsByNomClientEnArabe(clientReq.getNomClientEnArabe())) {
                throw new IllegalArgumentException("Nom de l'établissement / Clinique  déjà utilisé (FR ou AR)");
            }
            client.setNomClientEnArabe(clientReq.getNomClientEnArabe());
            client.setEmail(clientReq.getEmail());
            client.setTelephone(clientReq.getTelephone());
            if (clientRepository.existsByAdresse(clientReq.getAdresse())) {
                throw new IllegalArgumentException("Adresse déjà utilisée");
            }
            client.setAdresse(clientReq.getAdresse());
            client.setPays(clientReq.getPays());
            client.setVille(clientReq.getVille());
            client.setRue(clientReq.getRue());
            client.setCodePostal(clientReq.getCodePostal());
            client.setSpecialite(clientReq.getSpecialite());
            client.setStatus(EStatus.ACTIVE);
            client.setEmailVerificationStatus(EmailVerificationStatus.VERIFIED);
//            if (file != null && !file.isEmpty()) {
//                String fileName = fileStorageService.storeClientLogo(file, clientReq.getNomClient());
//                client.setImage(fileName);
//            }

//            if (contentType != null && !contentType.isEmpty()) {
//                String url = gcsService.generateUploadUrl(client.getNomClient(),contentType);
//                client.setImage(fileName);
//            }


            Client savedClient = clientRepository.save(client);

            String extension = getImageExtension(contentType);

            String fileName = buildLogoPath(
                    savedClient.getIdClient(),
                    client.getNomClient(),
                    extension
            );
            String path = baseUrl + '/' + fileName;
            ClientConfig config = buildDefaultClientConfig(client,clientReq,path);
            config.setClient(client);

            String url = gcsService.generateUploadUrl(fileName,contentType);
            savedClient.setImage(fileName);
            savedClient.setClientConfig(config);

            clientRepository.save(savedClient);

            Local local = buildDefaultLocal(savedClient,clientReq);
            localRepository.save(local);

            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setNom(clientReq.getNom());
            utilisateur.setPrenom(clientReq.getPrenom());
            utilisateur.setClient(savedClient);
            utilisateur.setEmail(clientReq.getEmail());
            utilisateur.setRole(EROLE.MEDECIN_PRINCIPAL);
            utilisateur.setStatus(EStatus.ACTIVE);
            String username = utilisateurService.generateUsername(clientReq.getNom(), clientReq.getPrenom());
            if (utilisateurRepository.existsByNomUtilisateur(username)) {
                throw new IllegalArgumentException("Utilisateur déjà utilisé");
            }
            utilisateur.setNomUtilisateur(username);
            var mdp = utilisateurService.generateRandomPassword(10);
            utilisateur.setMdp(passwordEncoder.encode(mdp));
            utilisateurService.saveUser(utilisateur);
            emailService.sendPasswordAndUserName(utilisateur,mdp);
//            emailService.sendOtpEmail(client.getEmail(), utilisateur.getPrenom(), otpSerivce.createEmailOtpForClient(client.getIdClient(), "Client Email Verification"));
            return url;

        } catch (IllegalArgumentException e) {
            throw new CustomBadRequestException(e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Une erreur est survenue lors de l'enregistrement du client");
        }
    }


    private ClientDto enableClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        client.setEmailVerificationStatus(EmailVerificationStatus.VERIFIED);
        client.setStatus(EStatus.ACTIVE);
        Client updatedClient = clientRepository.save(client);

        Utilisateur utilisateur = utilisateurRepository.findUtilisateurByClientIdClient(clientId).orElseThrow();
        String tempPassword = utilisateurService.generateRandomPassword(10);
        utilisateur.setMdp(passwordEncoder.encode(tempPassword));
        utilisateurRepository.save(utilisateur);
        emailService.sendPasswordAndUserName(utilisateur, tempPassword);
        return clientMapper.EntityToDto(updatedClient);
    }
    private Local buildDefaultLocal(Client client, ClientRegister clientReq) {
        Local local = new Local();
        local.setClient(client);
        local.setAdresse(clientReq.getAdresse());
        local.setCodePostal(clientReq.getCodePostal());
        local.setPays(clientReq.getPays());
        local.setVille(clientReq.getVille());
        local.setTelephone(clientReq.getTelephone());
        local.setMobile(clientReq.getTelephone());
        local.setClientCreatorId(client.getIdClient());
        local.setClient(client);

        if (clientReq.getLocationMaps() != null) {
            local.setLatitude(clientReq.getLocationMaps().getLatitude());
            local.setLongitude(clientReq.getLocationMaps().getLongitude());
            local.setDisplayName(clientReq.getLocationMaps().getDisplayName());
        }

        local.setNomLocal(clientReq.getNomClient());
        local.setNomLocalEnArabe(clientReq.getNomClientEnArabe());
        local.setStatus(EStatus.ACTIVE);
        return local;
    }

    private ClientConfig buildDefaultClientConfig(Client client,ClientRegister clientDto,String path) {
        ClientConfig config = new ClientConfig();
        config.setHeureDebutTravail(clientDto.getHeureDebut());
        config.setHeureFinTravail(clientDto.getHeureFin());
        config.setDureeRendezVous(clientDto.getDureeRdv());

        config.setUseDefaultMedicament(true);
        config.setUseDefaultForms(true);
        config.setUseDefaultDuree(true);
        config.setUseDefaultPosologie(true);
        config.setUseDefaultMotifs(true);
        config.setUseDefaultConduit(true);
        config.setUseDefaultParClinique(true);
        config.setUseDefaultOptionPatClinique(true);
        String htmlContent = defaultTemplateService.generateTemplateForClient(client);
        String headerJson = defaultTemplateService.generateDefaultHeaderJson(client,path);
        config.setHeaderConfigJson(headerJson);
        config.setHtmlContent(htmlContent);
        return config;
    }

    private boolean isValidImage(String contentType) {
        return contentType != null && (contentType.equals("image/png")
                || contentType.equals("image/jpeg")
                || contentType.equals("image/gif"));
    }

    public Boolean checkClientStatus(Long clientId) {
        Client client = clientRepository.findById(clientId).orElseThrow(() -> new RuntimeException("Client not found"));
        return client.getStatus().equals(EStatus.ACTIVE);
    }

    public void generateOtp(String email) {
        emailService.sendOtpEmail(email,
                otpSerivce.createEmailOtpForClient(email, "Client Email Verification"));
    }
//
//    public ClientDto changeLogo(Long id, String contentType) {
//        Client client = clientRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Client not found"));
//        if (contentType != null && !contentType.isEmpty()) {
////            if (!isValidImage(contentType)) {
////                throw new IllegalArgumentException("Type de fichier non autorisé");
////            }
//            String url = gcsService.generateUploadUrl(client.getImage(),contentType);
//            return clientMapper.EntityToDto(client);
//        } else {
//            throw new IllegalArgumentException("Fichier image est vide");
//        }
//    }

    public String changeLogo(Long id, String contentType) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        ClientConfig clientConfig = clientConfigRepository.findClientConfigByClientIdClient(client.getIdClient())
                .orElseThrow(() -> new RuntimeException("ClientConfig not found"));

        if (contentType != null && !contentType.isEmpty()) {
//            if (!isValidImage(imageLogo)) {
//                throw new IllegalArgumentException("Type de fichier non autorisé");
//            }
            String extension = getImageExtension(contentType);
            String fileName = buildLogoPath(
                    client.getIdClient(),
                    client.getNomClient(),
                    extension
            );

            String path = baseUrl + '/' + fileName;
            gcsService.delete(client.getImage());
            updateLogoUrlInHeaderConfig(clientConfig, path);
            return gcsService.generateUploadUrl(fileName,contentType);
        } else {
            throw new IllegalArgumentException("Fichier image est vide");
        }
    }

    private String getImageExtension(String contentType) {

        if (contentType == null) {
            throw new IllegalArgumentException("Content type cannot be null");
        }

        return switch (contentType) {

            case "image/png" -> "png";

            case "image/jpeg" -> "jpg";

            case "image/gif" -> "gif";

            default -> throw new IllegalArgumentException(
                    "Unsupported image type: " + contentType
            );
        };
    }

    private String buildLogoPath(Long clientId, String clientName, String extension) {

        String cleanName = clientName
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9]", "_");


        return  cleanName
                + "_"
                + clientId
                + "_logo."
                + extension;
    }


    private void updateLogoUrlInHeaderConfig(ClientConfig clientConfig, String newLogoUrl) {
        try {
            Map<String, Object> config = objectMapper.readValue(clientConfig.getHeaderConfigJson(), Map.class);

            Map<String, Object> logoConfig = (Map<String, Object>) config.get("logoConfig");
            logoConfig.put("url", newLogoUrl);

            clientConfig.setHeaderConfigJson(objectMapper.writeValueAsString(config));
            clientConfigRepository.save(clientConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
