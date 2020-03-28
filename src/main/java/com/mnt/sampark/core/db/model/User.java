package com.mnt.sampark.core.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends AuditableLongBaseEntity {

    @Column(length = 50, unique = true)
    private String username;

    @Column(length = 60)
    private String password;

    @Column(name = "default_account_id")
    private Long defaultAccountId;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH})
    @JoinTable(name = "tab_user_group_map", joinColumns = {
        @JoinColumn(name = "user_id", nullable = false, updatable = false)},
            inverseJoinColumns = {
                @JoinColumn(name = "core_security_group_id",
                        nullable = false, updatable = false)}
    )
    @Cascade({
        org.hibernate.annotations.CascadeType.DETACH,
        org.hibernate.annotations.CascadeType.LOCK,
        org.hibernate.annotations.CascadeType.REFRESH,
        org.hibernate.annotations.CascadeType.REPLICATE,})
    private List<SecurityGroup> roles = new ArrayList<SecurityGroup>();

    @Column(length = 60)
    private String type;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getDefaultAccountId() {
        return defaultAccountId;
    }

    public void setDefaultAccountId(Long defaultAccountId) {
        this.defaultAccountId = defaultAccountId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<SecurityGroup> getRoles() {
        return roles;
    }

    public void setRoles(List<SecurityGroup> roles) {
        this.roles = roles;
    }
}
