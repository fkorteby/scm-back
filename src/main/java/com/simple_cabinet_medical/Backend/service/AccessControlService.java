package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.model.*;
import com.simple_cabinet_medical.Backend.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component("authz")
public class AccessControlService {

//    private final UtilisateurRepository utilisateurRepository;
//
//    private static final Logger logger = LoggerFactory.getLogger(AccessControlService.class);
//
//    public AccessControlService(UtilisateurRepository utilisateurRepository) {
//        this.utilisateurRepository = utilisateurRepository;
//    }
//
//    public List<BasedObject> hasAccess(List<BasedObject> list, Long idUtilisateur) {
//        return list.stream()
//                .filter(obj -> hasAccess(obj, idUtilisateur, EAccessLevel.READ))
//                .toList();
//    }
//
//    public boolean hasAccess(BasedObject obj, Long idUtilisateur, EAccessLevel accessLevel) {
//        logger.info("Checking access for user ID: {} on object: {} with level: {}", idUtilisateur, obj, accessLevel);
//
//        // Vérifier si l'utilisateur existe
//        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(idUtilisateur);
//        if (utilisateurOpt.isEmpty()) {
//            return false;
//        }
//        Utilisateur utilisateur = utilisateurOpt.get();
//
//        // L'ADMIN a accès à tout
//        if (utilisateur.getRole().equals(EROLE.ADMIN)) {
//            return true;
//        }
//
//        // Vérification par type d'objet
//        if (obj instanceof Client) {
//            return checkClientAccess((Client) obj, utilisateur, accessLevel);
//        }
//        if (obj instanceof Patient) {
//            return checkPatientAccess((Patient) obj, utilisateur, accessLevel);
//        }
//        if (obj instanceof Consultation) {
//            return checkConsultationAccess((Consultation) obj, utilisateur, accessLevel);
//        }
//        if (obj instanceof Document) {
//            return checkDocumentAccess((Document) obj, utilisateur, accessLevel);
//        }
//        if (isCommonMedicalObject(obj)) {
//            return checkMedicalObjectAccess(obj, utilisateur, accessLevel);
//        }
//
//        return false;
//    }
//
//    private boolean checkClientAccess(Client client, Utilisateur utilisateur, EAccessLevel accessLevel) {
//        return accessLevel.equals(EAccessLevel.READ) && utilisateur.getClient().equals(client);
//    }
//
//    private boolean checkPatientAccess(Patient patient, Utilisateur utilisateur, EAccessLevel accessLevel) {
//        return accessLevel.equals(EAccessLevel.READ) && utilisateur.getClient().equals(patient.getClient());
//    }
//
//    private boolean checkConsultationAccess(Consultation consultation, Utilisateur utilisateur, EAccessLevel accessLevel) {
//        if (accessLevel.equals(EAccessLevel.WRITE) || accessLevel.equals(EAccessLevel.DELETE) || accessLevel.equals(EAccessLevel.UPDATE)) {
//            return utilisateur.getRole().equals(EROLE.MEDECIN) || consultation.getIdUtilisateur().equals(utilisateur.getIdUtilisateur());
//        }
//        if (accessLevel.equals(EAccessLevel.READ)) {
//            return utilisateur.getClient().equals(utilisateurRepository.findById(consultation.getIdUtilisateur()).get().getClient());
//        }
//        return false;
//    }
//
//    private boolean checkDocumentAccess(Document document, Utilisateur utilisateur, EAccessLevel accessLevel) {
//        if (accessLevel.equals(EAccessLevel.WRITE) || accessLevel.equals(EAccessLevel.DELETE) || accessLevel.equals(EAccessLevel.UPDATE)) {
//            return utilisateur.getRole().equals(EROLE.MEDECIN) || document.getIdUtilisateur().equals(utilisateur.getIdUtilisateur());
//        }
//        if (accessLevel.equals(EAccessLevel.READ)) {
//            return utilisateur.getClient().equals(utilisateurRepository.findById(document.getIdUtilisateur()).get().getClient());
//        }
//        return false;
//    }
//
//    private boolean checkMedicalObjectAccess(BasedObject obj, Utilisateur utilisateur, EAccessLevel accessLevel) {
//        if (accessLevel.equals(EAccessLevel.WRITE) || accessLevel.equals(EAccessLevel.DELETE) || accessLevel.equals(EAccessLevel.UPDATE)) {
//            return utilisateur.getRole().equals(EROLE.ADMIN) || utilisateur.getRole().equals(EROLE.MEDECIN);
//        }
//        if (accessLevel.equals(EAccessLevel.READ)) {
//            return utilisateur.getClient().equals(utilisateurRepository.findById(obj.getIdUtilisateur()).get().getClient());
//        }
//        return false;
//    }
//
//    private boolean isCommonMedicalObject(BasedObject obj) {
//        return (obj instanceof Local || obj instanceof Medicament || obj instanceof Forme ||
//                obj instanceof Conduite || obj instanceof Duree || obj instanceof Motif ||
//                obj instanceof OptionParaclinique || obj instanceof Paraclinique ||
//                obj instanceof Posologie || obj instanceof Template);
//    }
}
