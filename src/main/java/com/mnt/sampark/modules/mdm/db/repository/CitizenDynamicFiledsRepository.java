package com.mnt.sampark.modules.mdm.db.repository;

import com.mnt.sampark.modules.mdm.db.domain.CitizenDynamicFileds;
import org.springframework.stereotype.Repository;

@Repository("CitizenDynamicFiledsRepository")
public interface CitizenDynamicFiledsRepository extends CrudJpaSpecRepository<CitizenDynamicFileds, Long>{

}
