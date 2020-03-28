package com.mnt.sampark.core.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="core_account")
public class Account  extends AuditableLongBaseEntity{

	@Column(length=50)
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
