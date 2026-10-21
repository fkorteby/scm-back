package com.simple_cabinet_medical.Backend.importation.domain;

import java.sql.Date;

public record OldConsultation(
        long numCons,
        long numMal,
        Date dateCons,
        String motif,
        String diag,
        String resultatExamen,
        String resultatPara,
        String conduiteCons,
        String traitCons,
        String trait2Cons
) {}