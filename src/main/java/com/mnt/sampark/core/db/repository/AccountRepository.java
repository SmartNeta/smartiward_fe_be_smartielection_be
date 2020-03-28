package com.mnt.sampark.core.db.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mnt.sampark.core.db.model.Account;
import com.mnt.sampark.core.db.model.SecurityActions;

@Repository("AccountRepository")
public interface AccountRepository extends CrudRepository<Account, Long> {

}
