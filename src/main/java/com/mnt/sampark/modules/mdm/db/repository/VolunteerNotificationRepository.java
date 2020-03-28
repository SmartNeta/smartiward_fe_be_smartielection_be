package com.mnt.sampark.modules.mdm.db.repository;

import com.mnt.sampark.modules.mdm.db.domain.VolunteerNotification;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("VolunteerNotificationRepository")
public interface VolunteerNotificationRepository extends CrudJpaSpecRepository<VolunteerNotification, Long> {

    @Query(value = "select * from volunteer_notification vn where vn.assembly_constituency_id = :assemblyId or (vn.assembly_constituency_id is null and vn.parliamentary_constituency_id = (select a.parliamentary_constituency_id from assembly_constituency a where a.id = :assemblyId)) order by vn.createddate desc;", nativeQuery = true)
    public List<VolunteerNotification> findAllByAssemblyId(@Param("assemblyId") Long assemblyId);

}
