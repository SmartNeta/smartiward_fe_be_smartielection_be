package com.mnt.sampark.modules.mdm.db.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mnt.sampark.core.db.model.AuditableLongBaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "citizen_dynamic_fileds")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CitizenDynamicFileds extends AuditableLongBaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "label", length = 50)
    private String label;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}