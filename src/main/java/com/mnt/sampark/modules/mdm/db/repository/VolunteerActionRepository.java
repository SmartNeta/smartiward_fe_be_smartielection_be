package com.mnt.sampark.modules.mdm.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mnt.sampark.modules.mdm.db.domain.VolunteerAction;

@Repository("VolunteerActionRepository")
public interface VolunteerActionRepository extends CrudJpaSpecRepository<VolunteerAction, Long> {

	VolunteerAction findByStateAssemblyIdAndAction(Long stateId, String action);

	List<VolunteerAction> findAllByStateAssemblyIdAndVisibilityOrderBySequenceAsc(@Param("stateId") Long stateId, @Param("visibility") boolean b);

}
