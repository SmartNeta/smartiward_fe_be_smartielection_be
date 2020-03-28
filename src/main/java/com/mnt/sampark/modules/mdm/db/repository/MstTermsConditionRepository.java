package com.mnt.sampark.modules.mdm.db.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mnt.sampark.modules.mdm.db.domain.MstTermsCondition;

@Repository("MstTermsConditionRepository")
public interface MstTermsConditionRepository extends CrudRepository<MstTermsCondition, Long>{

}
