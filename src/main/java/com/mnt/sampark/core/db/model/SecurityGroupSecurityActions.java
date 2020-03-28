package com.mnt.sampark.core.db.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="core_securitygroup_securityactions")
public class SecurityGroupSecurityActions  {
	
	@EmbeddedId
	private SecurityGroupSecurityActionsId id;
	
	public SecurityGroupSecurityActions(){}
	
	public SecurityGroupSecurityActions(SecurityGroupSecurityActionsId id) {
		super();
		this.id = id;
	}

	public SecurityGroupSecurityActionsId getId() {
		return id;
	}

	public void setId(SecurityGroupSecurityActionsId id) {
		this.id = id;
	}
	
}
