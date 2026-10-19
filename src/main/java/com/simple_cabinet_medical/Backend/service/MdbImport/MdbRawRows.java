package com.simple_cabinet_medical.Backend.service.MdbImport;

import java.sql.Date;

/**
 * "Raw" DTOs (one row = one object), used only while streaming through the
 * MDB file, before conversion into JPA entities.
 */
public final class MdbRawRows {

    private MdbRawRows() {}

    public record RawPatient(long numMal, String nom, String prenom, Date ddn, String adresse,
                             String tel, String sexe, String situation, String profession,
                             String assurance, String antMed, String antChir, String antFam,
                             String autres) {
    }

    /** Common to both versions. conduite/trait2 are null for v1.0. */
    public record RawConsultation(long numCons, long numMal, Date dateCons, String motif, String diag,
                                  String rsltExamen, String rsltPara, String conduite_cons,
                                  String traitCons, String trait2Cons) {
    }

    /** presc table — only exists for v1.7. */
    public record RawPresc(long numCons, long numMal, String medic, String forme, String poso, String duree) {
    }
}