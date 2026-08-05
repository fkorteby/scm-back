package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.Dto.Pub.CompagnePubRequestDto;
import com.simple_cabinet_medical.Backend.model.*;
import com.simple_cabinet_medical.Backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;

@Service
public class CompagnePublicitaireServcie {

    private static final Logger log = (Logger) LoggerFactory.getLogger(CompagnePublicitaireServcie.class);

    private final CompagnePublicitaireRepository compagnePublicitaireRepository;
    private final AnnonceurRepository annonceurRepository;
    private final FileStorageService fileStorageService;
    private final AdsRepository adsRepository;

    public CompagnePublicitaireServcie(
            CompagnePublicitaireRepository compagnePublicitaireRepository,
            AnnonceurRepository annonceurRepository,
            FileStorageService fileStorageService, AdsRepository adsRepository) {
        this.compagnePublicitaireRepository = compagnePublicitaireRepository;
        this.annonceurRepository = annonceurRepository;
        this.fileStorageService = fileStorageService;
        this.adsRepository = adsRepository;
    }

    @Transactional
    public void create(CompagnePubRequestDto dto, MultipartFile file) {
        CompagnePublicitaire compagne = new CompagnePublicitaire();
        Annonceur annonceur = annonceurRepository.findById(dto.getAnnonceur())
                .orElseThrow(() -> new RuntimeException("Annonceur non trouvé avec l'ID : " + dto.getAnnonceur()));

        String wilayasString = dto.getWilayas() != null && !dto.getWilayas().isEmpty()
                ? "|" + String.join("|", dto.getWilayas()) + "|"
                : null;

        String specialitesString = dto.getSpecialites() != null && !dto.getSpecialites().isEmpty()
                ? "|" + String.join("|", dto.getSpecialites()) + "|"
                : null;

        compagne.setAnnonceur(annonceur);
        compagne.setBudjet(dto.getBudjet());
        compagne.setDescription(dto.getDescription());
        compagne.setFormatAffichePub(EFormatAffichePub.valueOf(dto.getFormatAffichePub()));
        compagne.setNomCompagnePub(dto.getNomCompagnePub());
        compagne.setStatus(EStatusCompagnePub.valueOf(dto.getStatus()));
        compagne.setType(ETypeAnnonce.valueOf(dto.getType()));
        compagne.setDateDebut(dto.getDateDebut());
        compagne.setDateFin(dto.getDateFin());
        compagne.setWilayaCible(wilayasString);
        compagne.setLien(dto.getLien());
        compagne.setSpecialiteCible(specialitesString);

        CompagnePublicitaire savedCompagne = compagnePublicitaireRepository.save(compagne);

        if (file != null && !file.isEmpty()) {
            try {
                log.info("Début traitement fichier pour la campagne - annonceurId={}, campagneId={}",
                        annonceur.getIdAnnonceur(), savedCompagne.getIdCompagnePub());

                String filePath = fileStorageService.storeVisuelCompangePub(
                        file,
                        annonceur.getIdAnnonceur(),
                        savedCompagne.getIdCompagnePub()
                );

                log.info("Fichier uploadé avec succès : {}", filePath);

                savedCompagne.setFilePath(filePath);
                compagnePublicitaireRepository.save(savedCompagne);

                String wilayasMongo = dto.getWilayas() != null
                        ? String.join(",", dto.getWilayas())
                        : "";

                String specialitesMongo = dto.getSpecialites() != null
                        ? String.join(",", dto.getSpecialites())
                        : "";

                Ads ads = new Ads(
                        savedCompagne.getIdCompagnePub(),
                        savedCompagne.getNomCompagnePub(),
                        savedCompagne.getDescription(),
                        savedCompagne.getFilePath(),
                        savedCompagne.getLien(),
                        wilayasMongo,
                        specialitesMongo,
                        savedCompagne.getDateDebut(),
                        savedCompagne.getDateFin()
                );
                adsRepository.save(ads);

                log.info("Campagne mise à jour en base avec le visuel - campagneId={}", savedCompagne.getIdCompagnePub());

            } catch (RuntimeException ex) {
                log.error("Erreur lors de l'upload du visuel pour campagneId={} : {}", savedCompagne.getIdCompagnePub(), ex.getMessage(), ex);
                // Tu peux relancer l'exception si tu veux que le controller renvoie une erreur
                throw ex;
            }
        } else {
            log.warn("Aucun fichier fourni pour la campagne - annonceurId={}, campagneId={}",
                    annonceur.getIdAnnonceur(), savedCompagne.getIdCompagnePub());
        }

    }

    @Transactional
    public void update(CompagnePubRequestDto dto, Long id) {

        CompagnePublicitaire compagne = compagnePublicitaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Compagne Publicitaire non trouvée avec l'ID : " + id));

        String wilayasString = null;
        if (dto.getWilayas() != null && !dto.getWilayas().isEmpty()) {
            wilayasString = "|" + dto.getWilayas().stream()
                    .map(String::trim)
                    .collect(Collectors.joining("|")) + "|";
        }

        String specialitesString = null;
        if (dto.getSpecialites() != null && !dto.getSpecialites().isEmpty()) {
            specialitesString = "|" + dto.getSpecialites().stream()
                    .map(String::trim)
                    .collect(Collectors.joining("|")) + "|";
        }

        compagne.setWilayaCible(wilayasString);
        compagne.setSpecialiteCible(specialitesString);

        compagne.setBudjet(dto.getBudjet());
        compagne.setDescription(dto.getDescription());
        compagne.setFormatAffichePub(EFormatAffichePub.valueOf(dto.getFormatAffichePub()));
        compagne.setNomCompagnePub(dto.getNomCompagnePub());
        compagne.setStatus(EStatusCompagnePub.valueOf(dto.getStatus()));
        compagne.setType(ETypeAnnonce.valueOf(dto.getType()));

        CompagnePublicitaire updatedCompagne =
                compagnePublicitaireRepository.save(compagne);

        // =========================
        // UPDATE MONGODB
        // =========================

        String wilayasMongo = dto.getWilayas() != null
                ? String.join(",", dto.getWilayas())
                : "";

        String specialitesMongo = dto.getSpecialites() != null
                ? String.join(",", dto.getSpecialites())
                : "";

        Ads ads = new Ads(
                updatedCompagne.getIdCompagnePub(),
                updatedCompagne.getNomCompagnePub(),
                updatedCompagne.getDescription(),
                updatedCompagne.getFilePath(),
                updatedCompagne.getLien(),
                wilayasMongo,
                specialitesMongo,
                updatedCompagne.getDateDebut(),
                updatedCompagne.getDateFin()
        );

        adsRepository.save(ads);
    }

    public void delete(Long id) {
        compagnePublicitaireRepository.deleteById(id);
    }

    public void changeVisuelOfCompagnePub(MultipartFile file, Long id) {
        CompagnePublicitaire compagnePublicitaire = compagnePublicitaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compagne Publicitaire non trouvée avec l'ID : " + id));

        if (file != null && !file.isEmpty()) {
            String filePath = fileStorageService.storeVisuelCompangePub(
                    file,
                    compagnePublicitaire.getAnnonceur().getIdAnnonceur(),
                    id
            );
            compagnePublicitaire.setLien(filePath);
            compagnePublicitaireRepository.save(compagnePublicitaire);
        } else {
            log.warn("Aucun fichier fourni ...");
        }
    }

}