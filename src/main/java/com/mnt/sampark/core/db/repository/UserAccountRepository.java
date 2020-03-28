package com.mnt.sampark.core.db.repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mnt.sampark.core.db.model.UserAccount;

@Repository("UserAccountRepository")
public interface UserAccountRepository extends CrudRepository<UserAccount, String> {
	
	Optional<UserAccount> findOneByAccountIdAndUserId(String accountId, Long userId);
	
	UserAccount findOneByAccountId(Long accountId);
	
	UserAccount findOneByAccountIdAndUserId(Long accountId,Long userId);
	
	@Query(value = "Select account_id From core_user_account Where user_id = ?1", nativeQuery = true  )
	List<BigInteger> findByUserId(Long userId);

}
