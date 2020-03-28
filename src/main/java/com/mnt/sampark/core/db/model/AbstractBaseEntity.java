/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mnt.sampark.core.db.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

/**
 *
 * @author aashish
 */
@MappedSuperclass
public abstract class AbstractBaseEntity implements BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", length = 36)
    protected String id;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            this.setId(UUID.randomUUID().toString());
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
