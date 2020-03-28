package com.mnt.sampark.modules.mdm.db.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mnt.sampark.core.db.model.AuditableLongBaseEntity;


import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="district")
@JsonIgnoreProperties(ignoreUnknown = true)
public class District extends AuditableLongBaseEntity {
    private static final long serialVersionUID = 1L;

    @Column(name="name", length=100)
    @NotNull(message="name can not be empty")
    private String name;

    @Column(name="no")
    @NotNull(message="Number can not be empty")
    private String no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_assembly_id")
    @NotNull(message = "State assembly can not be empty")
    private StateAssembly stateAssembly;
    
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

	public StateAssembly getStateAssembly() {
		return stateAssembly;
	}

	public void setStateAssembly(StateAssembly stateAssembly) {
		this.stateAssembly = stateAssembly;
	}

}
