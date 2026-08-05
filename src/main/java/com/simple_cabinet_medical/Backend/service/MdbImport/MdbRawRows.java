package com.simple_cabinet_medical.Backend.service.MdbImport;

import java.sql.Date;

/**
 * DTOs "bruts" (une ligne = un objet), utilisés uniquement pendant la lecture
 * en flux du fichier MDB, avant transformation en entités JPA.
 */
public final class MdbRawRows {

    private MdbRawRows() {}

    public record RawPatient(long numMal, String nom, String prenom, Date ddn, String adresse,
                             String tel, String sexe, String situation, String profession,
                             String assurance, String antMed, String antChir, String antFam,
                             String autres) {
    }

    /** Commun aux 2 versions. conduite/trait2 sont null pour la v1.0. */
    public record RawConsultation(long numCons, long numMal, Date dateCons, String motif, String diag,
                                  String rsltExamen, String rsltPara, String conduite,
                                  String traitCons, String trait2Cons) {
    }

    /** Table presc — n'existe que pour la v1.7. */
    public record RawPresc(long numCons, long numMal, String medic, String forme, String poso, String duree) {
    }
}