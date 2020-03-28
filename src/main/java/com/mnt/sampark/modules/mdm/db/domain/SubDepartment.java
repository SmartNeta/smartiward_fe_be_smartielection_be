package com.mnt.sampark.modules.mdm.db.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mnt.sampark.core.db.model.AuditableLongBaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "sub_department")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubDepartment extends AuditableLongBaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "name", length = 100)
    @NotNull(message = "Name can not be empty")
    private String name;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="department_id")
    @NotNull(message="Department can not be empty")
    private Department department;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
