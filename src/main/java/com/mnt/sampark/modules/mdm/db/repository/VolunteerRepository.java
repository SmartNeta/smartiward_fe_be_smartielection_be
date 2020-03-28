package com.mnt.sampark.modules.mdm.db.repository;

import com.mnt.sampark.modules.mdm.db.domain.Volunteer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository("VolunteerRepository")
public interface VolunteerRepository extends CrudJpaSpecRepository<Volunteer, Long> {

    Optional<Volunteer> findById(Long volunteerId);

    @Query(value = "select * from volunteer V where V.mobile = :mobile and V.assembly_constituency_id = :assemblyConstituencyId limit 1", nativeQuery = true)
    Volunteer findByMobileAndAssemblyConstituencyId(@Param("mobile") String mobile, @Param("assemblyConstituencyId") String assemblyConstituencyId);

    List<Volunteer> findByAssemblyConstituencyIdOrderByStatusDesc(Long assemblyConstituencyId);

    @Query(value = "select * from volunteer v where v.assembly_constituency_id IN (select A.id from assembly_constituency A where A.parliamentary_constituency_id IN (select P.id from parliamentary_constituency P where P.district_id IN (select D.id from district D where D.state_assembly_id = :stateId))) order by v.status desc", nativeQuery = true)
    List<Volunteer> findByStateIdOrderByStatusDesc(@Param("stateId") Long stateId);

    @Query(value = "select * from volunteer v where v.assembly_constituency_id IN (select A.id from assembly_constituency A where A.parliamentary_constituency_id = :pcId) order by v.status desc ;", nativeQuery = true)
    List<Volunteer> findByParliamentaryConstituencyIdOrderByStatusDesc(@Param("pcId") Long pcId);

    Volunteer findByMobile(String mobile);

    @Query(value = "select COUNT(*) from volunteer v where v.status != 'Deleted' and v.assembly_constituency_id IN (select A.id from assembly_constituency A where A.parliamentary_constituency_id IN (select P.id from parliamentary_constituency P where P.district_id IN (select D.id from district D where D.state_assembly_id = :state_id)));", nativeQuery = true)
    Long findAllCount(@Param("state_id") Long state_id);

    @Query(value = "select COUNT(*) from volunteer v where v.status != 'Deleted' and v.assembly_constituency_id in (select distinct W.assembly_constituency_id from ward W where W.id in :wardIds)", nativeQuery = true)
    Long findAllCountByWardIn(@Param("wardIds") List<Long> wardIds);

    @Query(value = "select COUNT(*) from volunteer v where v.assembly_constituency_id IN (select A.id from assembly_constituency A where A.parliamentary_constituency_id IN (select P.id from parliamentary_constituency P where P.district_id IN (select D.id from district D where D.state_assembly_id = :state_id))) and (v.last_login < NOW() AND v.last_login > NOW() - INTERVAL 7 DAY and v.status = 'Loggedin')", nativeQuery = true)
    Long findActiveCount(@Param("state_id") Long state_id);

    @Query(value = "select COUNT(*) from volunteer v where  v.assembly_constituency_id in (select distinct W.assembly_constituency_id from ward W where W.id in :wardIds) and (v.last_login < NOW() AND v.last_login > NOW() - INTERVAL 7 DAY) and v.status = 'Loggedin'", nativeQuery = true)
    Long findActiveCountByWardIn(@Param("wardIds") List<Long> wardIds);

    @Query(value = "select count(*) from volunteer v where v.assembly_constituency_id = :assemblyId and v.last_login > DATE_SUB(NOW(),INTERVAL 10 HOUR) and v.status = 'Loggedin'", nativeQuery = true)
    Long findActiveCountByAssemblyOnPieChart(@Param("assemblyId") Long assemblyId);

    @Query(value = "select COUNT(*) from volunteer v where v.assembly_constituency_id = :assemblyId and v.status != 'Loggedin'", nativeQuery = true)
    Long findInactiveCountByAssemblyOnPieChart(@Param("assemblyId") Long assemblyId);

    @Query(value = "select count(*) from volunteer v where v.assembly_constituency_id IN (select A.id from assembly_constituency A where A.parliamentary_constituency_id IN (select P.id from parliamentary_constituency P where P.district_id IN (select D.id from district D where D.state_assembly_id = :stateId))) and v.last_login > DATE_SUB(NOW(),INTERVAL 10 HOUR) and v.status = 'Loggedin'", nativeQuery = true)
    Long findActiveCountOnChartByState(@Param("stateId") Long stateId);

    @Query(value = "select count(*) from volunteer v where v.assembly_constituency_id IN (select A.id from assembly_constituency A where A.parliamentary_constituency_id IN (select P.id from parliamentary_constituency P where P.district_id IN (select D.id from district D where D.state_assembly_id = :stateId))) and v.status != 'Loggedin'", nativeQuery = true)
    Long findInactiveCountOnChartByState(@Param("stateId") Long stateId);

    @Query(value = "select cast(v.device_id as char), cast(v.device_type as char) from volunteer v where v.assembly_constituency_id = :assemblyConstituencyId and v.device_id is not null", nativeQuery = true)
    List<Object[]> findDeviceIdByAssemblyConstituencyId(@Param("assemblyConstituencyId") String assemblyConstituencyId);

    @Query(value = "select cast(v.device_id as char), cast(v.device_type as char) from volunteer v where v.assembly_constituency_id in (select AC.id from assembly_constituency AC where AC.parliamentary_constituency_id = :parliamentaryConstituencyId) and v.device_id is not null", nativeQuery = true)
    List<Object[]> findDeviceIdByParliamentaryConstituencyId(@Param("parliamentaryConstituencyId") String parliamentaryConstituencyId);

}
