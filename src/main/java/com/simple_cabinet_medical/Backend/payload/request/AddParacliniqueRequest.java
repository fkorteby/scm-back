package com.simple_cabinet_medical.Backend.payload.request;

public class AddParacliniqueRequest {

    private String examen;
    private String type;
    private Long idUtilisateur;

    public AddParacliniqueRequest(String examen, String type, Long idUtilisateur) {
        this.examen = examen;
        this.type = type;
        this.idUtilisateur = idUtilisateur;
    }

    public AddParacliniqueRequest() {}

    public String getExamen() {
        return examen;
    }

    public void setExamen(String examen) {
        this.examen = examen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Long idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }
}
