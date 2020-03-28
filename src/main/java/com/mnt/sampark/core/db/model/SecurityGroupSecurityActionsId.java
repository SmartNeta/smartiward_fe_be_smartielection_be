package com.mnt.sampark.core.db.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class SecurityGroupSecurityActionsId implements Serializable {

    private static final long serialVersionUID = 1L;

    public SecurityGroupSecurityActionsId() {
    }

    public SecurityGroupSecurityActionsId(Long securityGroupId, Long securityActionsId) {
        super();
        this.securityActionsId = securityActionsId;
        this.securityGroupId = securityGroupId;
    }

    @Column(name = "securityactions_id")
    private Long securityActionsId;

    @Column(name = "security_group_id")
    private Long securityGroupId;

    public Long getSecurityGroupId() {
        return securityGroupId;
    }

    public void setSecurityGroupId(Long securityGroupId) {
        this.securityGroupId = securityGroupId;
    }

    public Long getSecurityActionsId() {
        return securityActionsId;
    }

    public void setSecurityActionsId(Long securityActionsId) {
        this.securityActionsId = securityActionsId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SecurityGroupSecurityActionsId)) {
            return false;
        }
        SecurityGroupSecurityActionsId that = (SecurityGroupSecurityActionsId) o;
        return Objects.equals(getSecurityGroupId(), that.getSecurityGroupId())
                && Objects.equals(getSecurityActionsId(), that.getSecurityActionsId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSecurityGroupId(), getSecurityActionsId());
    }
}
