package com.mnt.sampark.modules.mdm.db.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mnt.sampark.core.db.model.AuditableLongBaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "assembly_constituency")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssemblyConstituency extends AuditableLongBaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "name", length = 100, unique = true)
    @NotNull(message = "name can not be empty")
    private String name;

    @Column(name = "no", length = 100)
    @NotNull(message = "Number can not be empty")
    private String no;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="parliamentary_constituency_id")
    @NotNull(message="Parliamentary constituency can not be empty")
    private ParliamentaryConstituency parliamentaryConstituency;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ParliamentaryConstituency getParliamentaryConstituency() {
		return parliamentaryConstituency;
	}

	public void setParliamentaryConstituency(ParliamentaryConstituency parliamentaryConstituency) {
		this.parliamentaryConstituency = parliamentaryConstituency;
	}

	public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }
}
