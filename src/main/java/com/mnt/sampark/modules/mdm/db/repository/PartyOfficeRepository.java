package com.mnt.sampark.modules.mdm.db.repository;

import com.mnt.sampark.modules.mdm.db.domain.PartyOffice;
import org.springframework.stereotype.Repository;

@Repository("PartyOfficeRepository")
public interface PartyOfficeRepository extends CrudJpaSpecRepository<PartyOffice, Long>{

}
