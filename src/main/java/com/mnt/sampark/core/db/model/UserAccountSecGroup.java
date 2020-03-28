package com.mnt.sampark.core.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="core_user_account_security_group")
public class UserAccountSecGroup extends LongIdBaseEntity {

	public UserAccountSecGroup(){}
	
	public UserAccountSecGroup(Long userAccountId,Long securityGroupId) {
		super();
		this.userAccountId = userAccountId;
		this.securityGroupId = securityGroupId;
	}
	
	public Long getUserAccountId() {
		return userAccountId;
	}

	public void setUserAccountId(Long userAccountId) {
		this.userAccountId = userAccountId;
	}

	@Column(name="user_account_id")
	private Long userAccountId;
	
	@Column(name="security_group_id")
	private Long securityGroupId;
	
	
	public Long getSecurityGroupId() {
		return securityGroupId;
	}
	public void setSecurityGroupId(Long securityGroupId) {
		this.securityGroupId = securityGroupId;
	}
	
	
		
	
}
