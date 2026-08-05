package com.simple_cabinet_medical.Backend.Projection;

public interface RapportSummaryProjection {
    Long getTotal();
    Long getMasculin();
    Long getFeminin();
    Long getAssures();
    Long getNonAssures();
}