package com.mnt.sampark.modules.mdm.db.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mnt.sampark.core.db.model.AuditableLongBaseEntity;

@Entity
@Table(name = "ward")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ward extends AuditableLongBaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "name", length = 100)
    @NotNull(message = "name can not be empty")
    private String name;

    @Column(name = "no", length = 100)
    @NotNull(message = "Number can not be empty")
    private String no;

    /*@Column(name = "type")
    @NotNull(message = "type can not be empty")
    private String type;*/

    @Column(name = "area_address", length = 250)
    private String areaAddress;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "assembly_constituency_id")
    @NotNull(message = "Assembly constituency can not be empty")
    private AssemblyConstituency assemblyConstituency;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAreaAddress() {
        return areaAddress;
    }

    public void setAreaAddress(String areaAddress) {
        this.areaAddress = areaAddress;
    }

    public AssemblyConstituency getAssemblyConstituency() {
        return assemblyConstituency;
    }

    public void setAssemblyConstituency(AssemblyConstituency assemblyConstituency) {
        this.assemblyConstituency = assemblyConstituency;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }
}
