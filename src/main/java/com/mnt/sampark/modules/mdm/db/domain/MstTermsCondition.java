package com.mnt.sampark.modules.mdm.db.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mnt.sampark.core.db.model.AuditableLongBaseEntity;

@Entity
@Table(name="mst_terms_condition")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MstTermsCondition extends AuditableLongBaseEntity {
	
	@Column(name="number")
	@NotNull(message="Number can not be empty")
	private Integer number;
	
	@Column(name="name")
	@NotNull
	@Size(min = 3, max = 20,message="Name must be between 3 and 20 characters")
	private String name;
	
	@Column(name="type",length=20)
	private String type;
	
	@Column(name="description",length=250)
	private String description;
	
	@Column(name="days")
	@NotNull(message="days can not be empty")
	private Integer days;
	
	private Integer dueDays;
	
	@Column(name="discount")
	private Float discount;
	
	@Column(name="is_epay")
	private Boolean isEPay;
	
	/*Only use below configuration if you have composition.*/
	@OneToMany(fetch=FetchType.LAZY, cascade = {CascadeType.MERGE,CascadeType.PERSIST},orphanRemoval=true)
	//@Fetch(FetchMode.JOIN)
	@JoinColumn(name="fk_mst_terms_condition_id",referencedColumnName="id")
	private List<MstTermsCondition_Phone> phones;
	
	

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getDueDays() {
		return dueDays;
	}

	public void setDueDays(Integer dueDays) {
		this.dueDays = dueDays;
	}

	public Float getDiscount() {
		return discount;
	}

	public void setDiscount(Float discount) {
		this.discount = discount;
	}

	public Boolean getIsEPay() {
		return isEPay;
	}

	public void setIsEPay(Boolean isEPay) {
		this.isEPay = isEPay;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	} 
	
	
	public List<MstTermsCondition_Phone> getPhones() {
		return phones;
	}

	public void setPhones(List<MstTermsCondition_Phone> phones) {
		this.phones = phones;
	}
	
	@Override
	public String toString() {
		return name;
	}
	

}
