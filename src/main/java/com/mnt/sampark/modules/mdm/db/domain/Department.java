package com.mnt.sampark.modules.mdm.db.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mnt.sampark.core.db.model.AuditableLongBaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "department")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Department extends AuditableLongBaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "name", length = 100)
    @NotNull(message = "Name can not be empty")
    private String name;

    @Column(name = "code", length = 20)
    @NotNull(message = "code can not be empty")
    private String code;


    public Department() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
