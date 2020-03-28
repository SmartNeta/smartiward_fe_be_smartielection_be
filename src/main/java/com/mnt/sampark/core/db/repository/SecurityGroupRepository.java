package com.mnt.sampark.core.db.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.mnt.sampark.core.db.model.SecurityGroup;
import com.mnt.sampark.modules.mdm.db.repository.CrudJpaSpecRepository;

@Repository("SecurityGroupRepository")
public interface SecurityGroupRepository extends CrudJpaSpecRepository<SecurityGroup, Long> {
	SecurityGroup findByName(String username);
	Optional<SecurityGroup> findById(Long id);
	List<SecurityGroup> findAll();

}
