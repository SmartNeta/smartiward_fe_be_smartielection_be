package com.mnt.sampark.core.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "core_user_account")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAccount extends LongIdBaseEntity {

    @Column(name = "account_id")
    private Long accountId;
    @Column(name = "user_id", length = 36)
    private Long userId;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
