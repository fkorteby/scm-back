package com.simple_cabinet_medical.Backend.dto;

public interface RapportSummaryProjection {
    Long getTotal();
    Long getMasculin();
    Long getFeminin();
    Long getAssures();
    Long getNonAssures();
}
