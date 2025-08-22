package com.simple_cabinet_medical.Backend.model;

import com.simple_cabinet_medical.Backend.utils.BasedObjectListener;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@MappedSuperclass
@EntityListeners({AuditingEntityListener.class, BasedObjectListener.class})
public abstract class BasedObject {

    @CreatedBy
    @Column(updatable = false)
    protected Long idUtilisateur;

    @Column(name = "client_creator_id", updatable = false)
    protected Long clientCreatorId;

    protected Date dateCreation;

    protected Date dateModification;

    public Long getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Long idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public Long getClientCreatorId() {
        return clientCreatorId;
    }

    public void setClientCreatorId(Long clientCreatorId) {
        this.clientCreatorId = clientCreatorId;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Date getDateModification() {
        return dateModification;
    }

    public void setDateModification(Date dateModification) {
        this.dateModification = dateModification;
    }
}
