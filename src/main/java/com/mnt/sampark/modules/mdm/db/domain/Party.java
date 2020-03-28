package com.mnt.sampark.modules.mdm.db.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mnt.sampark.core.db.model.AuditableLongBaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "party")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Party extends AuditableLongBaseEntity {
    private static final long serialVersionUID = 1L;

    @Column(name = "name", length = 250)
    @NotNull(message = "Name can not be empty")
    private String name;

    @Column(name = "code", length = 100)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_assembly_id")
    private StateAssembly stateAssembly;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id")
    private District district;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assembly_constituency_id")
    private AssemblyConstituency assemblyConstituency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id")
    private Ward ward;

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

    public StateAssembly getStateAssembly() {
        return stateAssembly;
    }

    public void setStateAssembly(StateAssembly stateAssembly) {
        this.stateAssembly = stateAssembly;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public AssemblyConstituency getAssemblyConstituency() {
        return assemblyConstituency;
    }

    public void setAssemblyConstituency(AssemblyConstituency assemblyConstituency) {
        this.assemblyConstituency = assemblyConstituency;
    }

    public Ward getWard() {
        return ward;
    }

    public void setWard(Ward ward) {
        this.ward = ward;
    }
}
