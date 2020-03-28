package com.mnt.sampark.modules.mdm.db.repository;

import com.mnt.sampark.modules.mdm.db.domain.Complaint;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository("ComplaintRepository")
public interface ComplaintRepository extends CrudJpaSpecRepository<Complaint, Long> {

    @Query(value = "select count(*) from  complaint c, citizen CT, booth B where c.citizen_id = CT.id and CT.booth_id = B.id and B.ward_id in :wardIds and c.status NOT IN('Resolved','Closed')", nativeQuery = true)
    Object[] getUnresolvedComplaints(@Param("wardIds") List<Long> wardIds);

    @Query(value = "select avg(NOW() - c.createddate) from  complaint c, citizen CT, booth B where c.citizen_id = CT.id and CT.booth_id = B.id and B.ward_id in :wardIds and c.status NOT IN('Resolved','Closed')", nativeQuery = true)
    Object[] getComplaintAverage(@Param("wardIds") List<Long> wardIds);

    @Query(value = "select * from complaint c where c.id =:id limit 1", nativeQuery = true)
    Complaint findByComplaintId(@Param("id") Long id);

    List<Complaint> findAllByUserId(Long userId);

    List<Complaint> findAllByStateAssemblyId(Long stateAssemblyId);

    List<Complaint> findAllByCitizenIdOrderByCreatedDateDesc(Long citizenId);

    @Query(value = "select * from complaint c where c.state_assembly_id IN (select UD.state_assembly_id from user_detail UD where UD.user_id = :userId) AND c.sub_department_id IN (SELECT UD.sub_department_id FROM user_detail UD where UD.user_id = :userId)", nativeQuery = true)
    List<Complaint> getComplaintByDepartment(@Param("userId") Long id);

    @Query(value = "select count(*) as total, SUM(CASE WHEN c.status = 'Resolved' THEN 1 ELSE 0 END) as resolved "
            + "from complaint c where MONTH(c.createddate) =:monthNumber and YEAR(c.createddate) =:year and c.state_assembly_id =:state_id", nativeQuery = true)
    public List<Object[]> getComplaintChartByMonthAndYear(@Param("monthNumber") int monthNumber, @Param("year") Long year, @Param("state_id") Long state_id);

    @Query(value = "select count(*) as total, SUM(CASE WHEN c.status = 'Resolved' THEN 1 ELSE 0 END) as resolved "
            + "from complaint c where  c.sub_department_id =:subDeptId and c.state_assembly_id = (select UD.state_assembly_id from user_detail UD where UD.user_id = :userId limit 1)", nativeQuery = true)
    public List<Object[]> getComplaintBySubdepartment(@Param("subDeptId") Long subDeptId, @Param("userId") Long userId);

    @Query(value = "select count(*) as total, SUM(CASE WHEN c.status = 'Resolved' THEN 1 ELSE 0 END) as resolved "
            + "from complaint c, citizen CT, booth B where c.citizen_id = CT.id and CT.booth_id = B.id and B.ward_id = :wardId and c.sub_department_id =:subDeptId", nativeQuery = true)
    public List<Object[]> getComplaintBySubdepartmentByWard(@Param("subDeptId") Long subDeptId, @Param("wardId") Long wardId);

    @Query(value = "select count(*) as total, SUM(CASE WHEN c.status = 'Resolved' THEN 1 ELSE 0 END) as resolved"
            + " from complaint c where  c.sub_department_id =:subDeptId and c.createddate BETWEEN"
            + " :startDate and :endDate and c.state_assembly_id = (select UD.state_assembly_id from user_detail UD where UD.user_id = :userId limit 1)", nativeQuery = true)
    public List<Object[]> getComplaintBySubdepartmentAndCreatedBeetween(@Param("subDeptId") Long id, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("userId") Long userId);

    @Query(value = "select count(*) as total, SUM(CASE WHEN c.status = 'Resolved' THEN 1 ELSE 0 END) as resolved"
            + " from complaint c, citizen CT, booth B where c.citizen_id = CT.id and CT.booth_id = B.id and B.ward_id = :wardId and c.sub_department_id =:subDeptId and c.createddate BETWEEN :startDate and :endDate", nativeQuery = true)
    public List<Object[]> getComplaintBySubdepartmentAndCreatedBeetweenByWard(@Param("subDeptId") Long id, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("wardId") Long wardId);

    @Query(value = "select count(*) from  complaint c where c.status NOT IN('Resolved','Closed') and c.state_assembly_id = :stateId  and c.sub_department_id = :subDepId", nativeQuery = true)
    public Object[] getUnresolvedComplaintsByDep(@Param("stateId") Long stateId, @Param("subDepId") Long subDepId);

    @Query(value = "select count(*) from  complaint c where c.status NOT IN('Resolved','Closed') and c.state_assembly_id = :stateId", nativeQuery = true)
    public Object[] getUnresolvedComplaints(@Param("stateId") Long stateId);

    @Query(value = "select avg(NOW() - c.createddate) from  complaint c where c.status NOT IN('Resolved','Closed') and c.state_assembly_id = :stateId and c.sub_department_id = :subDepId", nativeQuery = true)
    public Object[] getComplaintAverageByDep(@Param("stateId") Long stateId, @Param("subDepId") Long subDepId);

    @Query(value = "select avg(NOW() - c.createddate) from  complaint c where c.status NOT IN('Resolved','Closed') and c.state_assembly_id = :stateId", nativeQuery = true)
    public Object[] getComplaintAverage(@Param("stateId") Long stateId);

    @Query(value = "select count(*) from  complaint c, citizen CT, booth B where c.citizen_id = CT.id and CT.booth_id = B.id and B.ward_id in :wardIds and c.status NOT IN('Resolved','Closed') and c.sub_department_id = :subDepId", nativeQuery = true)
    public Object[] getUnresolvedComplaintsByWardAndDep(@Param("wardIds") List<Long> wardIds, @Param("subDepId") Long subDepId);

    @Query(value = "select count(*) from  complaint c, citizen CT, booth B where c.citizen_id = CT.id and CT.booth_id = B.id and B.ward_id = :wardId and c.status NOT IN('Resolved','Closed')", nativeQuery = true)
    public Object[] getUnresolvedComplaintsByWard(@Param("wardId") Long wardId);

    @Query(value = "select avg(NOW() - c.createddate) from  complaint c, citizen CT, booth B where c.citizen_id = CT.id and CT.booth_id = B.id and B.ward_id in :wardIds and c.status NOT IN('Resolved','Closed') and c.sub_department_id = :subDepId", nativeQuery = true)
    public Object[] getComplaintAverageByWardAndDep(@Param("wardIds") List<Long> wardIds, @Param("subDepId") Long subDepId);

    @Query(value = "select avg(NOW() - c.createddate) from  complaint c, citizen CT, booth B where c.citizen_id = CT.id and CT.booth_id = B.id and B.ward_id = :wardId and c.status NOT IN('Resolved','Closed')", nativeQuery = true)
    public Object[] getComplaintAverageByWard(@Param("wardId") Long wardId);

    @Query(value = "select count(*) as total, SUM(CASE WHEN c.status = 'Resolved' THEN 1 ELSE 0 END) as resolved "
            + "from complaint c where MONTH(c.createddate) =:monthNumber and YEAR(c.createddate) =:year and c.sub_department_id =:subDeptId and c.state_assembly_id = :state_id", nativeQuery = true)
    public List<Object[]> getComplaintChartByMonthAndYearAndDeptSubDept(@Param("monthNumber") int monthNumber, @Param("year") Long year, @Param("subDeptId") Long subDeptId, @Param("state_id") Long state_id);

    @Query(value = "select count(*) as total, SUM(CASE WHEN c.status = 'Resolved' THEN 1 ELSE 0 END) as resolved "
            + "from complaint c where MONTH(c.createddate) =:monthNumber and YEAR(c.createddate) =:year and "
            + "c.sub_department_id in (select id from sub_department s where s.department_id =:deptId) and c.state_assembly_id =:state_id", nativeQuery = true)
    public List<Object[]> getComplaintChartByMonthAndYearAndDept(@Param("monthNumber") int monthNumber, @Param("year") Long year, @Param("deptId") Long deptId, @Param("state_id") Long state_id);

    @Query(value = "select * from complaint c where SUBSTRING(c.createddate,1,10) = SUBSTRING(CURDATE(),1,10)", nativeQuery = true)
    public List<Complaint> findAllTodaysCreatedComplaints();

    @Query(value = "select * from complaint c where c.tentative_date_of_completion < CURDATE() and  c.status not in ('Resolved','closed')", nativeQuery = true)
    public List<Complaint> findAllPendingComplaints();

    @Query(value = "select * from complaint c where SUBSTRING(c.tentative_date_of_completion,1,10) = SUBSTRING(CURDATE(),1,10)", nativeQuery = true)
    public List<Complaint> findAllTodaysDueComplaints();

    @Query(value = "select c.incident_id, c.name from complaint c where SUBSTRING(c.createddate,1,10) = SUBSTRING(CURDATE(),1,10) and c.state_assembly_id = :state_id", nativeQuery = true)
    public List<Object[]> findAllTodaysCreatedComplaints(@Param("state_id") Long state_id);

    @Query(value = "select c.incident_id, c.name from complaint c , citizen CT, booth B where c.citizen_id = CT.id and CT.booth_id = B.id and B.ward_id in :wardIds and SUBSTRING(c.createddate,1,10) = SUBSTRING(CURDATE(),1,10)", nativeQuery = true)
    public List<Object[]> findAllTodaysCreatedComplaintsBY(@Param("wardIds") List<Long> wardIds);

    @Query(value = "select c.incident_id, c.name  from complaint c where c.tentative_date_of_completion < CURDATE() and  c.status not in ('Resolved','closed') and c.state_assembly_id = :state_id", nativeQuery = true)
    public List<Object[]> findAllPendingComplaints(@Param("state_id") Long state_id);

    @Query(value = "select c.incident_id, c.name from complaint c , citizen CT, booth B where c.citizen_id = CT.id and CT.booth_id = B.id and B.ward_id in :wardIds and  c.tentative_date_of_completion < CURDATE() and  c.status not in ('Resolved','closed')", nativeQuery = true)
    public List<Object[]> findAllPendingComplaintsByWard(@Param("wardIds") List<Long> wardIds);

    @Query(value = "select c.incident_id, c.name from complaint c, citizen CT, booth B where c.citizen_id = CT.id and CT.booth_id = B.id and B.ward_id in :wardIds and SUBSTRING(c.tentative_date_of_completion,1,10) = SUBSTRING(CURDATE(),1,10)", nativeQuery = true)
    public List<Object[]> findAllTodaysDueComplaintsByWard(@Param("wardIds") List<Long> wardIds);

    @Query(value = "select c.incident_id, c.name  from complaint c where SUBSTRING(c.tentative_date_of_completion,1,10) = SUBSTRING(CURDATE(),1,10) and c.state_assembly_id = :state_id", nativeQuery = true)
    public List<Object[]> findAllTodaysDueComplaints(@Param("state_id") Long state_id);

    @Query(value = "select c.incident_id, c.name from complaint c , citizen CT, booth B where c.citizen_id = CT.id and CT.booth_id = B.id and B.ward_id in :wardIds and c.sub_department_id IN(select SD.id from sub_department SD where SD.department_id = :deptId) and  SUBSTRING(c.createddate,1,10) = SUBSTRING(CURDATE(),1,10)", nativeQuery = true)
    public List<Object[]> findAllTodaysCreatedComplaintsByDeptByWard(@Param("wardIds") List<Long> wardIds, @Param("deptId") Long deptId);

    @Query(value = "select c.incident_id, c.name from complaint c where c.sub_department_id IN(select SD.id from sub_department SD where SD.department_id = :deptId) and  SUBSTRING(c.createddate,1,10) = SUBSTRING(CURDATE(),1,10) and c.state_assembly_id = :state_id", nativeQuery = true)
    public List<Object[]> findAllTodaysCreatedComplaintsByDept(@Param("state_id") Long state_id, @Param("deptId") Long deptId);

    @Query(value = "select c.incident_id, c.name from complaint c , citizen CT, booth B where c.citizen_id = CT.id and CT.booth_id = B.id and B.ward_id in :wardIds and c.sub_department_id IN(select SD.id from sub_department SD where SD.department_id = :deptId) and  SUBSTRING(c.tentative_date_of_completion,1,10) = SUBSTRING(CURDATE(),1,10)", nativeQuery = true)
    public List<Object[]> findAllTodaysDueComplaintsByDeptByWard(@Param("wardIds") List<Long> wardIds, @Param("deptId") Long deptId);

    @Query(value = "select c.incident_id, c.name from complaint c where c.sub_department_id IN(select SD.id from sub_department SD where SD.department_id = :deptId) and  SUBSTRING(c.tentative_date_of_completion,1,10) = SUBSTRING(CURDATE(),1,10) and c.state_assembly_id = :state_id", nativeQuery = true)
    public List<Object[]> findAllTodaysDueComplaintsByDept(@Param("state_id") Long state_id, @Param("deptId") Long deptId);

    @Query(value = "select c.incident_id, c.name from complaint c where c.sub_department_id IN(select SD.id from sub_department SD where SD.department_id = :deptId) and c.tentative_date_of_completion < CURDATE() and  c.status not in ('Resolved','closed') and c.state_assembly_id = :state_id", nativeQuery = true)
    public List<Object[]> findAllPendingComplaintsByDept(@Param("state_id") Long state_id, @Param("deptId") Long deptId);

    @Query(value = "select c.incident_id, c.name from complaint c , citizen CT, booth B where c.citizen_id = CT.id and CT.booth_id = B.id and B.ward_id in :wardIds and c.sub_department_id IN(select SD.id from sub_department SD where SD.department_id = :deptId) and c.tentative_date_of_completion < CURDATE() and  c.status not in ('Resolved','closed')", nativeQuery = true)
    public List<Object[]> findAllPendingComplaintsByDeptByWard(@Param("wardIds") List<Long> wardIds, @Param("deptId") Long deptId);

    @Query(value = "select c.incident_id, c.name from complaint c where c.sub_department_id = :subDeptId and  SUBSTRING(c.createddate,1,10) = SUBSTRING(CURDATE(),1,10) and c.state_assembly_id = :stateId", nativeQuery = true)
    public List<Object[]> findAllTodaysCreatedComplaintsBySubDept(@Param("stateId") Long stateId, @Param("subDeptId") Long subDeptId);

    @Query(value = "select c.incident_id, c.name from complaint c, citizen CT, booth B where c.citizen_id = CT.id and CT.booth_id = B.id and B.ward_id in :wardIds and c.sub_department_id = :subDeptId and  SUBSTRING(c.createddate,1,10) = SUBSTRING(CURDATE(),1,10) ", nativeQuery = true)
    public List<Object[]> findAllTodaysCreatedComplaintsBySubDeptByWard(@Param("wardIds") List<Long> wardIds, @Param("subDeptId") Long subDeptId);

    @Query(value = "select c.incident_id, c.name from complaint c where c.sub_department_id = :subDeptId and  SUBSTRING(c.tentative_date_of_completion,1,10) = SUBSTRING(CURDATE(),1,10) and c.state_assembly_id = :stateId", nativeQuery = true)
    public List<Object[]> findAllTodaysDueComplaintsBySubDept(@Param("stateId") Long stateId, @Param("subDeptId") Long subDeptId);

    @Query(value = "select c.incident_id, c.name from complaint c , citizen CT, booth B where c.citizen_id = CT.id and CT.booth_id = B.id and B.ward_id in :wardIds and c.sub_department_id = :subDeptId and  SUBSTRING(c.tentative_date_of_completion,1,10) = SUBSTRING(CURDATE(),1,10)", nativeQuery = true)
    public List<Object[]> findAllTodaysDueComplaintsBySubDeptByWard(@Param("wardIds") List<Long> wardIds, @Param("subDeptId") Long subDeptId);

    @Query(value = "select c.incident_id, c.name from complaint c where c.sub_department_id = :subDeptId and c.tentative_date_of_completion < CURDATE() and  c.status not in ('Resolved','closed') and c.state_assembly_id = :stateId", nativeQuery = true)
    public List<Object[]> findAllPendingComplaintsBySubDept(@Param("stateId") Long stateId, @Param("subDeptId") Long subDeptId);

    @Query(value = "select c.incident_id, c.name from complaint c , citizen CT, booth B where c.citizen_id = CT.id and CT.booth_id = B.id and B.ward_id in :wardIds and c.sub_department_id = :subDeptId and c.tentative_date_of_completion < CURDATE() and  c.status not in ('Resolved','closed')", nativeQuery = true)
    public List<Object[]> findAllPendingComplaintsBySubDeptByWard(@Param("wardIds") List<Long> wardIds, @Param("subDeptId") Long subDeptId);

    @Query(value = "select * from complaint c where SUBSTRING(c.createddate,1,10) = SUBSTRING(CURDATE(),1,10) and c.state_assembly_id = (select UD.state_assembly_id from user_detail UD where UD.user_id = :userId limit 1)", nativeQuery = true)
    public List<Complaint> findAllTodaysCreatedComplaintsByWard(@Param("userId") Long userId);

    @Query(value = "select * from complaint c where c.state_assembly_id = (select UD.state_assembly_id from user_detail UD where UD.user_id = :userId limit 1)", nativeQuery = true)
    List<Complaint> findAllByUserStateId(@Param("userId") Long id);

    //Total Number Of Complaints Reported vs Total Resolved By Month chart query
    @Query(value = "select count(*) as total, SUM(CASE WHEN c.status = 'Resolved' THEN 1 ELSE 0 END) as resolved "
            + "from complaint c, citizen CT, booth B where MONTH(c.createddate) =:monthNumber and c.citizen_id = CT.id and CT.booth_id = B.id and B.ward_id =:wardId and "
            + "YEAR(c.createddate) =:year and c.sub_department_id =:subDeptId and c.state_assembly_id = :state_id", nativeQuery = true)
    public List<Object[]> getComplaintChartByMonthAndYearAndDeptSubDeptAndWard(@Param("monthNumber") int monthNumber, @Param("year") Long year, @Param("subDeptId") Long subDeptId, @Param("state_id") Long state_id, @Param("wardId") Long wardId);

    @Query(value = "select count(*) as total, SUM(CASE WHEN c.status = 'Resolved' THEN 1 ELSE 0 END) as resolved "
            + "from complaint c, citizen CT, booth B where MONTH(c.createddate) =:monthNumber and YEAR(c.createddate) =:year and c.citizen_id = CT.id and CT.booth_id = B.id and B.ward_id =:wardId and "
            + "c.sub_department_id in (select id from sub_department s where s.department_id =:deptId) and c.state_assembly_id = :state_id", nativeQuery = true)
    public List<Object[]> getComplaintChartByMonthAndYearAndDeptAndWard(@Param("monthNumber") int monthNumber, @Param("year") Long year, @Param("deptId") Long deptId, @Param("state_id") Long state_id, @Param("wardId") Long wardId);

    @Query(value = "select count(*) as total, SUM(CASE WHEN c.status = 'Resolved' THEN 1 ELSE 0 END) as resolved "
            + "from complaint c, citizen CT, booth B where MONTH(c.createddate) =:monthNumber and c.citizen_id = CT.id and CT.booth_id = B.id and B.ward_id =:wardId and "
            + "YEAR(c.createddate) =:year and c.state_assembly_id = :state_id", nativeQuery = true)
    public List<Object[]> getComplaintChartByMonthAndYearAndWard(@Param("monthNumber") int monthNumber, @Param("year") Long year, @Param("state_id") Long state_id, @Param("wardId") Long wardId);

    @Query(value = "select count(*) as total, SUM(CASE WHEN c.status = 'Resolved' THEN 1 ELSE 0 END) as resolved from complaint c where c.state_assembly_id = (select UD.state_assembly_id from user_detail UD where UD.user_id = :userId limit 1)", nativeQuery = true)
    public List<Object[]> getAllResolvedComplaintsSummary(@Param("userId") Long userId);

    @Query(value = "select count(*) as total, SUM(CASE WHEN c.status = 'Resolved' THEN 1 ELSE 0 END) as resolved from complaint c, citizen CT, booth B where c.citizen_id = CT.id and CT.booth_id = B.id and B.ward_id =:wardId", nativeQuery = true)
    public List<Object[]> getAllResolvedComplaintsSummaryByWard(@Param("wardId") Long wardId);

    @Query(value = "select count(*) as total, SUM(CASE WHEN c.status = 'Resolved' THEN 1 ELSE 0 END) as resolved"
            + " from complaint c where c.createddate BETWEEN"
            + " :startDate and :endDate and c.state_assembly_id = (select UD.state_assembly_id from user_detail UD where UD.user_id = :userId limit 1)", nativeQuery = true)
    public List<Object[]> getComplaintByCreatedBeetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("userId") Long userId);

    @Query(value = "select count(*) as total, SUM(CASE WHEN c.status = 'Resolved' THEN 1 ELSE 0 END) as resolved"
            + " from complaint c, citizen CT, booth B where c.citizen_id = CT.id and CT.booth_id = B.id and B.ward_id = :wardId and c.createddate BETWEEN :startDate and :endDate", nativeQuery = true)
    public List<Object[]> getComplaintByCreatedBeetweenByWard(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("wardId") Long wardId);

}
