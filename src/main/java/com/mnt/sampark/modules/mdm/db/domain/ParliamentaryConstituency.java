package com.mnt.sampark.modules.mdm.db.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mnt.sampark.core.db.model.AuditableLongBaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "parliamentary_constituency")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParliamentaryConstituency extends AuditableLongBaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "name", length = 100)
    @NotNull(message = "name can not be empty")
    private String name;

    @Column(name = "no", length = 100)
    @NotNull(message = "Number can not be empty")
    private String no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id")
    @NotNull(message = "District constituency can not be empty")
    private District district;

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

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

}
