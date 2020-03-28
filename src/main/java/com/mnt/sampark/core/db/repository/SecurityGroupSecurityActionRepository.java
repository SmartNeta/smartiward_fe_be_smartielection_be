package com.mnt.sampark.core.db.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mnt.sampark.core.db.model.SecurityGroupSecurityActions;
import com.mnt.sampark.core.db.model.SecurityGroupSecurityActionsId;

@Repository
public interface SecurityGroupSecurityActionRepository extends CrudRepository<SecurityGroupSecurityActions, SecurityGroupSecurityActionsId> {

    @Query(value = "From SecurityGroupSecurityActions Where id.securityGroupId = ?1")
    List<SecurityGroupSecurityActions> findBySecurityGroupId(Long securityGroupId);

    @Query(value = "Select securityactions_id From core_securitygroup_securityactions Where security_group_id = ?1", nativeQuery = true)
    List<BigInteger> findBySecurityActionsIdByGroupId(Long securityGroupId);

    @Query(value = "Select distinct s.name from core_securitygroup_securityactions m inner join core_security_actions s on s.id = m.securityactions_id\n"
            + " where m.security_group_id in ?1", nativeQuery = true)
    List<String> findAllSecurityActionsBySecurityGroupIn(List<Long> securityGroupId);
}
