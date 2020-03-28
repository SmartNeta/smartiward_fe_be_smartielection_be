package com.mnt.sampark.modules.mdm.db.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mnt.sampark.core.db.model.LongIdBaseEntity;

@Entity
@Table(name="mst_terms_condition_phone")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MstTermsCondition_Phone extends LongIdBaseEntity {
	
	//@ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
	//@Cascade(value={CascadeType.ALL})
	//@JoinColumn(name = "mst_terms_condition_id")
	//private MstTermsCondition mstTermsCondition;
	
	@Column(name="number")
	@NotNull(message="Phone can not be empty")
	private Integer number;
	
	
	@Column(name="type",length=20)
	private String type;
	
	@Column(name="description",length=250)
	private String description;
	
	
	@Column(name="is_primary")
	private Boolean isPrimary;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

/*	public MstTermsCondition getMstTermsCondition() {
		return mstTermsCondition;
	}

	public void setMstTermsCondition(MstTermsCondition mstTermsCondition) {
		this.mstTermsCondition = mstTermsCondition;
	}*/

	public Boolean getIsPrimary() {
		return isPrimary;
	}

	public void setIsPrimary(Boolean isPrimary) {
		this.isPrimary = isPrimary;
	} 
	
	@Override
	public boolean equals(Object obj) {
		return this.number.equals( ((MstTermsCondition_Phone)obj).getNumber());
	}
	
	@Override
	public int hashCode() {
		return this.number.hashCode();
	}
	

}
