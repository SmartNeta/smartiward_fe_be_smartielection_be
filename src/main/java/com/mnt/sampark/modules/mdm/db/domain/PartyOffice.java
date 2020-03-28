package com.mnt.sampark.modules.mdm.db.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mnt.sampark.core.db.model.AuditableLongBaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "party_office")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartyOffice extends AuditableLongBaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "name", length = 100)
    @NotNull(message = "Name can not be empty")
    private String name;

    @Column(name = "address", length = 100)
    private String address;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="assembly_constituency_id")
    @NotNull(message="Assembly Constituency can not be empty")
    private AssemblyConstituency assemblyConstituency;

    @Column(name = "latitude", length = 100)
    private Double latitude;

    @Column(name = "longitude", length = 100)
    private Double longitude;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public AssemblyConstituency getAssemblyConstituency() {
        return assemblyConstituency;
    }

    public void setAssemblyConstituency(AssemblyConstituency assemblyConstituency) {
        this.assemblyConstituency = assemblyConstituency;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}

