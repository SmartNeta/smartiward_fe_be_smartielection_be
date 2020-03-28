package com.mnt.sampark.core.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "core_security_group")
public class SecurityGroup extends LongIdBaseEntity {

    @Column(name = "name", length = 255, unique = true)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    public SecurityGroup(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public SecurityGroup(String name) {
        this.name = name;
    }

    public SecurityGroup() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
