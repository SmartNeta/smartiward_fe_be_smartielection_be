package com.mnt.sampark.modules.mdm.db.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mnt.sampark.core.db.model.AuditableLongBaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "state_assembly")
@JsonIgnoreProperties(ignoreUnknown = true)
public class StateAssembly extends AuditableLongBaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "state")
    @NotNull(message = "state can not be empty")
    private String state;

    @Column(name = "volunteer_approval", columnDefinition = "boolean default true")
    private Boolean volunteerApproval = true;

    @Column(name = "create_citizen", columnDefinition = "boolean default true")
    private Boolean createCitizen = true;

    @Column(name = "update_citizen", columnDefinition = "boolean default true")
    private Boolean updateCitizen = true;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean getVolunteerApproval() {
        return volunteerApproval;
    }

    public void setVolunteerApproval(Boolean volunteerApproval) {
        this.volunteerApproval = volunteerApproval;
    }

    public Boolean getCreateCitizen() {
        return createCitizen;
    }

    public void setCreateCitizen(Boolean createCitizen) {
        this.createCitizen = createCitizen;
    }

    public Boolean getUpdateCitizen() {
        return updateCitizen;
    }

    public void setUpdateCitizen(Boolean updateCitizen) {
        this.updateCitizen = updateCitizen;
    }

}
