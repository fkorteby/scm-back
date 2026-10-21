package com.simple_cabinet_medical.Backend.service.MdbImport;

import java.sql.Date;

public final class MdbRawRows {

    private MdbRawRows() {}

    public record RawPatient(long numMal, String nom, String prenom, Date ddn, String adresse,
                             String tel, String sexe, String situation, String profession,
                             String assurance, String antMed, String antChir, String antFam,
                             String autres) {
    }

    public record RawConsultation(long numCons, long numMal, Date dateCons, String motif, String diag,
                                  String rsltExamen, String rsltPara, String conduite_cons,
                                  String traitCons, String trait2Cons) {
    }

    public record RawPresc(long numCons, long numMal, String medic, String forme, String poso, String duree) {
    }
}