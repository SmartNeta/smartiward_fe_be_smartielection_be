package com.mnt.sampark.core.db.repository;

import org.springframework.data.repository.CrudRepository;

import com.mnt.sampark.core.db.model.SecurityActions;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository("SecurityActionsRepository")
public interface SecurityActionsRepository extends CrudRepository<SecurityActions, Long> {

    public Optional<SecurityActions> findById(Long securityActionsId);

}
