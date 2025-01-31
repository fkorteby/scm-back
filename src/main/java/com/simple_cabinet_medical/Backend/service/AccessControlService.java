package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.model.*;
import com.simple_cabinet_medical.Backend.repository.ClientRepository;
import com.simple_cabinet_medical.Backend.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccessControlService {

    private final UtilisateurRepository utilisateurRepository;
    private final ClientRepository clientRepository;

    Logger logger = LoggerFactory.getLogger(AccessControlService.class);

    public AccessControlService(UtilisateurRepository utilisateurRepository, ClientRepository clientRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.clientRepository = clientRepository;
    }


    public List<BasedObject> hasAccess (List<BasedObject> list , Long idUtilisateur) {
        try {
            return  list.stream().filter(
                    basedObject -> hasAccess(basedObject, idUtilisateur, EAccessLevel.READ)
            ).toList();

        } catch (Exception e){
            logger.error(e.toString());
            return null;
        }
    }


    public boolean hasAccess (BasedObject obj , Long idUtilisateur, EAccessLevel accessLevel) {

        //check if user exists
        if (utilisateurRepository.findById(idUtilisateur).isEmpty()) {
            return false;
        } else {
            // check if the user is admin
            Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur).get();
            if (utilisateur.getRole().equals(EROLE.ADMIN)) {
                return true;
            }
        }

        // check if obj is type of client
        if (obj instanceof Client) {
            if (accessLevel.equals(EAccessLevel.READ)) {
                if (((Client) obj).getIdClient().equals(utilisateurRepository.findById(idUtilisateur).get().getClient().getIdClient())) {
                    return true;
                } else {
                    return false;
                }
            }

        }

        // check if obj is type of patient
        if (obj instanceof Patient) {
            if (accessLevel.equals(EAccessLevel.WRITE) || accessLevel.equals(EAccessLevel.DELETE) || accessLevel.equals(EAccessLevel.UPDATE)) {
                if (((Patient) obj).getIdUtilisateur().equals(idUtilisateur)) {
                    return true;
                }

            }
            if (accessLevel.equals(EAccessLevel.READ)) {
                Client objClient = utilisateurRepository.findById(((Patient) obj).getIdUtilisateur()).get().getClient();
                Client utilisateurClient = utilisateurRepository.findById(idUtilisateur).get().getClient();

                if (objClient.getIdClient().equals(utilisateurClient.getIdClient())) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        // check if obj is type of Ordonnance
        if (obj instanceof Ordonnance) {
            if (accessLevel.equals(EAccessLevel.WRITE) || accessLevel.equals(EAccessLevel.DELETE) || accessLevel.equals(EAccessLevel.UPDATE)) {
                if (((Ordonnance) obj).getIdUtilisateur().equals(idUtilisateur)) {
                    return true;
                }

            }
            if (accessLevel.equals(EAccessLevel.READ)) {
                Client objClient = utilisateurRepository.findById(((Ordonnance) obj).getIdUtilisateur()).get().getClient();
                Client utilisateurClient = utilisateurRepository.findById(idUtilisateur).get().getClient();

                if (objClient.getIdClient().equals(utilisateurClient.getIdClient())) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        // check if obj is type of Consultation
        if (obj instanceof Consultation) {
            if (accessLevel.equals(EAccessLevel.WRITE) || accessLevel.equals(EAccessLevel.DELETE) || accessLevel.equals(EAccessLevel.UPDATE)) {
                if (((Consultation) obj).getIdUtilisateur().equals(idUtilisateur)) {
                    return true;
                }
            }

            if (accessLevel.equals(EAccessLevel.READ)) {
                Client objClient = utilisateurRepository.findById(((Consultation) obj).getIdUtilisateur()).get().getClient();
                Client utilisateurClient = utilisateurRepository.findById(idUtilisateur).get().getClient();

                if (objClient.getIdClient().equals(utilisateurClient.getIdClient())) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        // check if obj is type of Document
        if (obj instanceof Document) {
            if (accessLevel.equals(EAccessLevel.WRITE) || accessLevel.equals(EAccessLevel.DELETE) || accessLevel.equals(EAccessLevel.UPDATE)) {
                if (((Document) obj).getIdUtilisateur().equals(idUtilisateur)) {
                    return true;
                }

            }
            if (accessLevel.equals(EAccessLevel.READ)) {
                Client objClient = utilisateurRepository.findById(((Consultation) obj).getIdUtilisateur()).get().getClient();
                Client utilisateurClient = utilisateurRepository.findById(idUtilisateur).get().getClient();

                if (objClient.getIdClient().equals(utilisateurClient.getIdClient())) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        // check if obj is type of Template
        if (obj instanceof Template) {
            if (accessLevel.equals(EAccessLevel.WRITE) || accessLevel.equals(EAccessLevel.DELETE) || accessLevel.equals(EAccessLevel.UPDATE)) {
                if (((Template) obj).getIdUtilisateur().equals(idUtilisateur)) {
                    return true;
                }
            }
            if (accessLevel.equals(EAccessLevel.READ)) {
                Client objClient = utilisateurRepository.findById(((Template) obj).getIdUtilisateur()).get().getClient();
                Client utilisateurClient = utilisateurRepository.findById(idUtilisateur).get().getClient();

                if (objClient.getIdClient().equals(utilisateurClient.getIdClient())) {
                    return true;
                } else {
                    return false;
                }
            }

            //if idUtilisateur is null (default) return true
        }

        // medecin principal update delete create parametres

        // check if obj is type of Local
        if ((obj instanceof Local) || (obj instanceof Medicament) || (obj instanceof Forme) || (obj instanceof Conduite) ||
        (obj instanceof Duree) || (obj instanceof Motif) || (obj instanceof OptionParaclinique) || (obj instanceof Paraclinique) ||
        (obj instanceof Posologie) || (obj instanceof Template)) {

            if (accessLevel.equals(EAccessLevel.WRITE) || accessLevel.equals(EAccessLevel.DELETE) || accessLevel.equals(EAccessLevel.UPDATE)) {
               Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur).get();
               if ((utilisateur.getRole().equals(EROLE.ADMIN)) || (utilisateur.getRole().equals(EROLE.MEDECIN))) {
                   return true;
               } else {
                   return false;
               }
            }
            if (accessLevel.equals(EAccessLevel.READ)) {
                Client objClient = utilisateurRepository.findById(((BasedObject) obj).getIdUtilisateur()).get().getClient();
                Client utilisateurClient = utilisateurRepository.findById(idUtilisateur).get().getClient();

                if (objClient.getIdClient().equals(utilisateurClient.getIdClient())) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        return false;
    }
}
