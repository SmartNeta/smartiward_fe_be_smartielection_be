/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mnt.sampark.core.db.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 *
 */
@MappedSuperclass
public abstract class AuditableLongBaseEntity extends LongIdBaseEntity implements Auditable {

    private static final long serialVersionUID = 1L;

    @Column(name = "createdby", nullable = false, length = 50, updatable = false)
    protected String createdBy;

    @Column(name = "createddate", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createdDate;

    @Column(name = "modifiedby", nullable = false, length = 50)
    protected String modifiedBy;

    @Column(name = "modifieddate", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date modifiedDate;

    @PrePersist
    public void prePersist() {
        if (this.getCreatedDate() == null) {
            this.setCreatedDate(new Date());
        }
        if (this.getModifiedDate() == null) {
            this.setModifiedDate(new Date());
        }

        Subject currentUser = SecurityUtils.getSubject();
        User user = (User) currentUser.getPrincipal();

        if (this.getCreatedBy() == null) {
            if (user == null) {
                this.setCreatedBy("sys");
                this.setModifiedBy("sys");
            } else {

                this.setCreatedBy(user.getUsername());
                this.setModifiedBy(user.getUsername());
            }
        }

    }

    @PreUpdate
    public void preUpdate() {
        this.setModifiedDate(new Date());

        if (this.getModifiedBy() == null) {
            Subject currentUser = SecurityUtils.getSubject();
            User user = (User) currentUser.getPrincipal();

            if (user == null) {
                this.setModifiedBy("sys");
            } else {
                this.setModifiedBy(user.getUsername());
            }
        }
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

}
