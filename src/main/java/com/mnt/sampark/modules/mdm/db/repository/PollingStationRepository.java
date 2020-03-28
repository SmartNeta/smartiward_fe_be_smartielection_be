package com.mnt.sampark.modules.mdm.db.repository;

import com.mnt.sampark.modules.mdm.db.domain.PollingStation;
import org.springframework.stereotype.Repository;

@Repository("PollingStationRepository")
public interface PollingStationRepository extends CrudJpaSpecRepository<PollingStation, Long>{

}
