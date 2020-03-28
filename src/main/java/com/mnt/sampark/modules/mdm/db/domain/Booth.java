package com.mnt.sampark.modules.mdm.db.domain;

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
@Table(name = "booth")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Booth extends AuditableLongBaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "address", length = 250)
    private String address;

    @Column(name = "name", length = 100)
    @NotNull(message = "name can not be empty")
    private String name;

    @Column(name = "no", length = 100)
    @NotNull(message = "Number can not be empty")
    private String no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id")
    @NotNull(message = "Ward can not be empty")
    private Ward ward;

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

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
