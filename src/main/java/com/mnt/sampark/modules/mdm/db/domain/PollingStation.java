package com.mnt.sampark.modules.mdm.db.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mnt.sampark.core.db.model.AuditableLongBaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "polling_station")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PollingStation extends AuditableLongBaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "name", length = 100)
    @NotNull(message = "Name can not be empty")
    private String name;

    @Column(name = "number")
    @NotNull(message = "Number can not be empty")
    private String number;

    @Column(name = "address", length = 100)
    private String address;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ward_id")
    @NotNull(message="Ward can not be empty")
    private Ward ward;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Ward getWard() {
        return ward;
    }

    public void setWard(Ward ward) {
        this.ward = ward;
    }
}
