package com.mnt.sampark.core.db.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mnt.sampark.core.db.model.UserAccountSecGroup;

@Repository
public interface UserAccountSecurityGroupRepository extends CrudRepository<UserAccountSecGroup, Long> {

	List<UserAccountSecGroup> findByUserAccountId(Long userAccountId);
	
	UserAccountSecGroup findByUserAccountIdAndSecurityGroupId(Long userAcountId,Long secGroupId);
}
