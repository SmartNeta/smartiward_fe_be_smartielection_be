package com.mnt.sampark.modules.mdm.db.repository;

import com.mnt.sampark.modules.mdm.db.domain.Booth;
import com.mnt.sampark.modules.mdm.db.domain.Citizen;
import com.mnt.sampark.modules.mdm.tools.AddressCountDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("CitizenRepository")
public interface CitizenRepository extends CrudJpaSpecRepository<Citizen, Long> {

    public Optional<Citizen> findById(Long citizenId);

    public Citizen findByVoterId(String voterId);

//    @Query(value = "select * from citizen c where c.booth_id in (booth)", nativeQuery = true)
//    Page findAllByBoothIn(List<Booth> booth, Pageable pageable);
    List<Citizen> findByBoothIn(List<Booth> booth);

    @Query(value = "select count(*) from citizen C where C.booth_id IN (select B.id from booth B where B.ward_id = :wardId)", nativeQuery = true)//previous query.. before removing booth forign key from citizen table
    public Number getTotalVotersCountByWardId(@Param("wardId") Long wardId);

    @Query(value = "select count(*) from citizen C where C.booth_id = :boothId", nativeQuery = true)//previous query.. before removing booth forign key from citizen table
    public Number getTotalVotersCountByBoothId(@Param("boothId") Long boothId);

    @Query(value = "select count(*) from citizen C where C.booth_id IN (select B.id from booth B where B.ward_id in (select W.id from ward W where W.assembly_constituency_id = :assemblyConstituencyId))", nativeQuery = true)//previous query.. before removing booth forign key from citizen table
    public Number getTotalVotersCountByAssemblyConstituencyId(@Param("assemblyConstituencyId") Long assemblyConstituencyId);

    @Query(value = "select count(DISTINCT C.address) from citizen C where C.booth_id IN (select B.id from booth B where B.ward_id = :wardId)", nativeQuery = true)
    public Number getTotalHousesCountByWardId(@Param("wardId") Long wardId);

    @Query(value = "select count(DISTINCT C.address) from citizen C where C.booth_id = :boothId", nativeQuery = true)
    public Number getTotalHousesCountByBoothId(@Param("boothId") Long boothId);

    @Query(value = "select count(DISTINCT C.address) from citizen C where C.booth_id IN (select B.id from booth B where B.ward_id in (select W.id from ward W where W.assembly_constituency_id = :assemblyConstituencyId))", nativeQuery = true)
    public Number getTotalHousesCountByAssemblyConstituencyId(@Param("assemblyConstituencyId") Long assemblyConstituencyId);

    @Query(value = "select COUNT(*) from citizen c where c.booth_id IN ( select B.id from booth B where B.ward_id IN (select W.id from ward W where W.assembly_constituency_id IN (select A.id from assembly_constituency A where A.parliamentary_constituency_id IN (select P.id from parliamentary_constituency P where P.district_id IN (select D.id from district D where D.state_assembly_id = :stateId))))) and "
            + "(c.last_login < NOW() AND c.last_login > NOW() - INTERVAL 7 DAY and c.status = 'Loggedin')", nativeQuery = true)
    public Object getActiveMember(@Param("stateId") Long stateId);

    @Query(value = "select COUNT(*) from citizen c where c.booth_id IN ( select B.id from booth B where B.ward_id in :wardIds ) and (c.last_login < NOW() AND c.last_login > NOW() - INTERVAL 7 DAY and c.status = 'Loggedin')", nativeQuery = true)
    public Object getActiveMemberByWardIn(@Param("wardIds") List<Long> wardIds);

    @Query(value = "select COUNT(*) from citizen c where c.booth_id IN ( select B.id from booth B where B.ward_id IN (select W.id from ward W where W.assembly_constituency_id IN (select A.id from assembly_constituency A where A.parliamentary_constituency_id IN (select P.id from parliamentary_constituency P where P.district_id IN (select D.id from district D where D.state_assembly_id = :stateId))))) and c.last_login is not null", nativeQuery = true)
    public Object getAllMember(@Param("stateId") Long stateId);

    @Query(value = "select COUNT(*) from citizen c where c.booth_id IN ( select B.id from booth B where B.ward_id in :wardIds) and c.last_login is not null", nativeQuery = true)
    public Object getAllMemberByWardIn(@Param("wardIds") List<Long> wardIds);

    @Query(value = "select count(*) from citizen C where C.booth_id IN (select B.id from booth B where B.ward_id = :wardId ) and C.responded_status = :status", nativeQuery = true)
    public Number getTotalByWardIdAndStatus(@Param("wardId") Long wardId, @Param("status") String status);

    @Query(value = "select count(*) from citizen C where C.booth_id = :boothId and C.responded_status = :status", nativeQuery = true)
    public Number getTotalByBoothIdAndStatus(@Param("boothId") Long boothId, @Param("status") String status);

    @Query(value = "select count(*) from citizen C where C.booth_id IN (select B.id from booth B where B.ward_id in (select W.id from ward W where W.assembly_constituency_id = :assemblyConstituencyId)) and C.responded_status = :status", nativeQuery = true)
    public Number getTotalByAssemblyConstituencyIdAndStatus(@Param("assemblyConstituencyId") Long assemblyConstituencyId, @Param("status") String status);

    @Query(value = "select count(*) from citizen c inner join booth b on b.id = c.booth_id where b.ward_id = :wardId and c.responded_status = 'responded' GROUP by c.address ;", nativeQuery = true)
    public Number[] getCitizenRespondedByWardId(@Param("wardId") Long wardId);

    @Query(value = "SELECT  "
            + "COUNT(DISTINCT IF(c.responded_status = 'responded', c.address ,null)) responded, "
            + "COUNT(DISTINCT IF(c.responded_status = 'call back', c.address ,null)) as callback,  "
            + "COUNT(DISTINCT IF(c.responded_status = 'not at home', c.address ,null)) as notathome,  "
            + "COUNT(DISTINCT IF(c.responded_status = 'refused', c.address ,null)) as refused  "
            + "FROM citizen c WHERE c.responded_status IS NOT NULL AND c.booth_id =:boothId group by c.address", nativeQuery = true)
    public List<Object[]> getHouseRespondedByBoothId(@Param("boothId") Long boothId);

    @Query(value = "SELECT  "
            + "COUNT(DISTINCT IF(c.responded_status = 'responded', c.address ,null)) responded, "
            + "COUNT(DISTINCT IF(c.responded_status = 'call back', c.address ,null)) as callback,  "
            + "COUNT(DISTINCT IF(c.responded_status = 'not at home', c.address ,null)) as notathome,  "
            + "COUNT(DISTINCT IF(c.responded_status = 'refused', c.address ,null)) as refused  "
            + "FROM citizen c INNER JOIN booth b ON b.id = c.booth_id WHERE c.responded_status IS NOT NULL AND b.ward_id =:wardId group by c.address", nativeQuery = true)
    public List<Object[]> getHouseRespondedByWardId(@Param("wardId") Long wardId);

    @Query(value = "SELECT  "
            + "COUNT(DISTINCT IF(c.responded_status = 'responded', c.address ,null)) responded, "
            + "COUNT(DISTINCT IF(c.responded_status = 'call back', c.address ,null)) as callback,  "
            + "COUNT(DISTINCT IF(c.responded_status = 'not at home', c.address ,null)) as notathome,  "
            + "COUNT(DISTINCT IF(c.responded_status = 'refused', c.address ,null)) as refused  "
            + "FROM citizen c INNER JOIN booth b ON b.id = c.booth_id WHERE c.responded_status IS NOT NULL AND b.ward_id IN (SELECT W.id FROM ward W WHERE W.assembly_constituency_id =:assemblyConstituencyId) group by c.address", nativeQuery = true)
    public List<Object[]> getHouseRespondedByAssemblyConstituencyId(@Param("assemblyConstituencyId") Long assemblyConstituencyId);

    @Query(value = "select max(C.age) from citizen C where  C.state = :state", nativeQuery = true)
    public Number getMaxAgeByState(@Param("state") String state);

    @Query(value = "select MAX(C.age) from citizen C where C.booth_id IN (select B.id from booth B where B.ward_id = :wardId ) ", nativeQuery = true)
    public Number getMaxAgeByWard(@Param("wardId") Long wardId);

    @Query(value = "select MAX(C.age) from citizen C where C.booth_id = :booth ", nativeQuery = true)
    public Number getMaxAgeByBooth(@Param("booth") Long booth);

    @Query(value = "select c.voter_id, concat(c.address, ' ', c.pincode) as address, c.pincode from citizen c where c.latitude is null or c.longitude is null ORDER BY RAND() limit 2000", nativeQuery = true)
    public List<Object[]> findAllCitizensWhereLatitudeAndLongitudeIsNull();

    @Query(value = "select c.assembly_no, c.ward_no, c.booth_no, c.srno, c.pincode, c.address, c.voter_id, c.first_name, c.family_name, concat(c.age,'') as age, c.gender, c.mobile, c.voter_segmentation, c.party_preference, c.voted, c.responded_status, c.latitude, c.longitude, c.volunteer_mobile, c.printed, c.voter_slip_sms_sent_on_mobile from citizen c where c.booth_id in (select b.id from booth b where b.ward_id =?1)", nativeQuery = true)
    public List<Object[]> getByWard(Long id);

    @Query(value = "select c.assembly_no, c.ward_no, c.booth_no, c.srno, c.pincode, c.address, c.voter_id, c.first_name, c.family_name, concat(c.age,'') as age, c.gender, c.mobile, c.voter_segmentation, c.party_preference, c.voted, c.responded_status, c.latitude, c.longitude, c.volunteer_mobile, c.printed, c.voter_slip_sms_sent_on_mobile from citizen c where c.booth_id in (select b.id from booth b where b.ward_id in (select w.id from ward w where w.assembly_constituency_id = ?1))", nativeQuery = true)
    public List<Object[]> getByAssembly(Long id);

    @Query(value = "select c.assembly_no, c.ward_no, c.booth_no, c.srno, c.pincode, c.address, c.voter_id, c.first_name, c.family_name, concat(c.age,'') as age, c.gender, c.mobile, c.voter_segmentation, c.party_preference, c.voted, c.responded_status, c.latitude, c.longitude, c.volunteer_mobile, c.printed, c.voter_slip_sms_sent_on_mobile from citizen c where c.booth_id in (select b.id from booth b where b.ward_id in (select w.id from ward w where w.assembly_constituency_id in (select a.id from assembly_constituency a where a.parliamentary_constituency_id = ?1)))", nativeQuery = true)
    public List<Object[]> getByPC(Long id);

    @Query(value = "select c.assembly_no, c.ward_no, c.booth_no, c.srno, c.pincode, c.address, c.voter_id, c.first_name, c.family_name, concat(c.age,'') as age, c.gender, c.mobile, c.voter_segmentation, c.party_preference, c.voted, c.responded_status, c.latitude, c.longitude, c.volunteer_mobile, c.printed, c.voter_slip_sms_sent_on_mobile from citizen c where c.booth_id in (select b.id from booth b where b.ward_id in (select w.id from ward w where w.assembly_constituency_id in (select a.id from assembly_constituency a where a.parliamentary_constituency_id in (select P.id from parliamentary_constituency P where P.district_id IN (select D.id from district D where D.state_assembly_id = ?1)))))", nativeQuery = true)
    public List<Object[]> getByState(Long id);

    @Query(value = "select C.address as address, count(*) as count from citizen C where C.booth_id in (select b.id from booth b where b.ward_id in(select w.id from ward w where w.assembly_constituency_id = ?1 )) group by C.address order by count desc limit ?2 offset ?3", nativeQuery = true)
    public List<AddressCountDto> nuOfVotersPerHouseByAssembly(Long id, int limit, int offset);

    @Query(value = "select C.address as address, count(*) as count from citizen C where C.booth_id in (select b.id from booth b where b.ward_id = ?1) group by C.address order by count desc limit ?2 offset ?3", nativeQuery = true)
    public List<AddressCountDto> nuOfVotersPerHouseByWard(Long id, int limit, int offset);

    @Query(value = "select C.address as address, count(*) as count from citizen C where C.booth_id = ?1 group by C.address order by count desc limit ?2 offset ?3 ", nativeQuery = true)
    public List<AddressCountDto> nuOfVotersPerHouseByBooth(Long id, int limit, int offset);

    @Query("SELECT NEW com.mnt.sampark.modules.mdm.tools.AddressCountDto(count(*) as count, C.address as address) "
            + "FROM com.mnt.sampark.modules.mdm.db.domain.Citizen C "
            + "where C.booth.id in (select b.id from com.mnt.sampark.modules.mdm.db.domain.Booth b where b.ward.id in(select w.id from com.mnt.sampark.modules.mdm.db.domain.Ward w where w.assemblyConstituency.id = ?1)) group by address")
    public Page<AddressCountDto> nuOfVotersPerHouseByAssembly(Long id, Pageable pageable);

    @Query(value = "select count(distinct ci.booth_id) as booths, count(distinct ci.address) as address, count(*) as voters from citizen ci where ci.booth_id in (select b.id from booth b where b.ward_id in (select w.id from ward w where w.assembly_constituency_id = ?1 ))", nativeQuery = true)
    public List<Object[]> getOverallReport(Long assemblyId);

}
