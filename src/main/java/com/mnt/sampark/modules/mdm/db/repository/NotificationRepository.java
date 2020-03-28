package com.mnt.sampark.modules.mdm.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mnt.sampark.modules.mdm.db.domain.Notification;
import java.util.Optional;

@Repository("NotificationRepository")
public interface NotificationRepository extends CrudJpaSpecRepository<Notification, Long> {

    Optional<Notification> findById(Long notificationId);

    @Query(value = "select count(*) from notification n where n.complaint_id IN (select C.id from complaint C where C.citizen_id = :id ) and n.status is null", nativeQuery = true)
    Number myNotificationCount(@Param("id") Long id);

    @Query(value = "select * from notification n where n.complaint_id IN (select C.id from complaint C where C.citizen_id = :id ) and n.status is null order by n.createddate desc", nativeQuery = true)
    List<Notification> myNotificationsByCitizenId(@Param("id") Long id);
}
