package com.simple_cabinet_medical.Backend.Permission;

import com.simple_cabinet_medical.Backend.model.BasedObject;
import com.simple_cabinet_medical.Backend.repository.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


@Component
public class EntityFetcher {

    private final ConsultationRepository consultationRepository;
    private final DocumentRepository documentRepository;
    private final TraitementRepository traitementRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PatientRepository patientRepository;
    private final CertificatTypeRepository certificatTypeRepository;
    private final ClientConfigRepository clientConfigRepository;
    private final ClientRepository clientRepository;
    private final DiagnosticRepository diagnosticRepository;
    private final DureeRepository dureeRepository;
    private final ExamenParacliniqueTypeRepository examenParacliniqueTypeRepository;
    private final ConduiteRepository conduiteRepository;
    private final FormeRepository formeRepository;
    private final LettreOrientationTypeRepository lettreOrientationTypeRepository;
    private final LocalRepository localRepository;
    private final MedicamentRepository medicamentRepository;
    private final MotifRepository motifRepository;
    private final OptionParacliniqueRepository optionParacliniqueRepository;
    private final OrdonnanceTypeRepository ordonnanceTypeRepository;
    private final ParacliniqueRepository paracliniqueRepository;
    private final PosologieRepository posologieRepository;
    private final RendezVousRepository rendezVousRepository;

    public EntityFetcher(@Lazy ConsultationRepository consultationRepository,
                         @Lazy PatientRepository patientRepository,
                         @Lazy DocumentRepository documentRepository,
                         @Lazy TraitementRepository traitementRepository,
                         @Lazy UtilisateurRepository utilisateurRepository,
                         @Lazy CertificatTypeRepository certificatTypeRepository,
                         @Lazy ClientConfigRepository clientConfigRepository,
                         @Lazy ClientRepository clientRepository,
                         @Lazy DiagnosticRepository diagnosticRepository,
                         @Lazy DureeRepository dureeRepository,
                         @Lazy ExamenParacliniqueTypeRepository examenParacliniqueTypeRepository,
                         @Lazy ConduiteRepository conduiteRepository,
                         @Lazy FormeRepository formeRepository,
                         @Lazy LettreOrientationTypeRepository lettreOrientationTypeRepository,
                         @Lazy LocalRepository localRepository,
                         @Lazy MedicamentRepository medicamentRepository,
                         @Lazy MotifRepository motifRepository,
                         @Lazy OptionParacliniqueRepository optionParacliniqueRepository,
                         @Lazy OrdonnanceTypeRepository ordonnanceTypeRepository,
                         @Lazy ParacliniqueRepository paracliniqueRepository,
                         @Lazy PosologieRepository posologieRepository,
                         @Lazy RendezVousRepository rendezVousRepository) {
        this.consultationRepository = consultationRepository;
        this.documentRepository = documentRepository;
        this.traitementRepository = traitementRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.patientRepository = patientRepository;
        this.certificatTypeRepository = certificatTypeRepository;
        this.clientConfigRepository = clientConfigRepository;
        this.clientRepository = clientRepository;
        this.diagnosticRepository = diagnosticRepository;
        this.dureeRepository = dureeRepository;
        this.examenParacliniqueTypeRepository = examenParacliniqueTypeRepository;
        this.conduiteRepository = conduiteRepository;
        this.formeRepository = formeRepository;
        this.lettreOrientationTypeRepository = lettreOrientationTypeRepository;
        this.localRepository = localRepository;
        this.medicamentRepository = medicamentRepository;
        this.motifRepository = motifRepository;
        this.optionParacliniqueRepository = optionParacliniqueRepository;
        this.ordonnanceTypeRepository = ordonnanceTypeRepository;
        this.paracliniqueRepository = paracliniqueRepository;
        this.posologieRepository = posologieRepository;
        this.rendezVousRepository = rendezVousRepository;
    }

    public BasedObject getEntity(String type, Long id) {
        switch (type) {
            case "Consultation":
                return consultationRepository.findById(id).orElse(null);
            case "Document":
                return documentRepository.findById(id).orElse(null);
            case "Traitement":
                return traitementRepository.findById(id).orElse(null);
            case "Patient":
                return patientRepository.findById(id).orElse(null);
            case "CertificatType":
                return certificatTypeRepository.findById(id).orElse(null);
//            case "ClientConfig":
//                return clientConfigRepository.findById(id).orElse(null);
//            case "Client":
//                return clientRepository.findById(id).orElse(null);
            case "Conduite":
                return conduiteRepository.findById(id).orElse(null);
            case "Diagnostic":
                return diagnosticRepository.findById(id).orElse(null);
            case "Duree":
                return dureeRepository.findById(id).orElse(null);
            case "ExamenParacliniqueType":
                return examenParacliniqueTypeRepository.findById(id).orElse(null);
            case "Forme":
                return formeRepository.findById(id).orElse(null);
            case "LettreOrientationType":
                return lettreOrientationTypeRepository.findById(id).orElse(null);
            case "Local":
                return localRepository.findById(id).orElse(null);
            case "Medicament":
                return medicamentRepository.findById(id).orElse(null);
            case "Motif":
                return motifRepository.findById(id).orElse(null);
            case "OptionParaclinique":
                return optionParacliniqueRepository.findById(id).orElse(null);
            case "OrdonnanceType":
                return ordonnanceTypeRepository.findById(id).orElse(null);
            case "Paraclinique":
                return paracliniqueRepository.findById(id).orElse(null);
            case "Posologie":
                return posologieRepository.findById(id).orElse(null);
            case "RendezVous":
                return rendezVousRepository.findById(id).orElse(null);
            default:
                return null;
        }
    }

}

