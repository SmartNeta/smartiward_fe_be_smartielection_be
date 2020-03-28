package com.mnt.sampark.modules.mdm.db.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mnt.sampark.core.db.model.AuditableLongBaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "volunteer_action")
@JsonIgnoreProperties(ignoreUnknown = true)
public class VolunteerAction extends AuditableLongBaseEntity implements Comparable<VolunteerAction> {

    private static final long serialVersionUID = 1L;

    @Column(name = "action", length = 200)
    @NotNull(message = "header can not be empty")
    private String action;

    @Column(name = "label", length = 200)
    @NotNull(message = "label can not be empty")
    private String label;

    @Column(name = "sequence", length = 100)
    private Integer sequence = 0;

    @Column(name = "visibility", length = 100)
    private Boolean visibility = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_assembly_id")
    @NotNull(message = "State assembly can not be empty")
    private StateAssembly stateAssembly;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(Boolean visibility) {
        this.visibility = visibility;
    }

    public StateAssembly getStateAssembly() {
        return stateAssembly;
    }

    public void setStateAssembly(StateAssembly stateAssembly) {
        this.stateAssembly = stateAssembly;
    }

    @Override
    public int compareTo(VolunteerAction o) {
        return (this.sequence - o.sequence);
    }

}
