package com.mnt.sampark.core.db.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mnt.sampark.core.db.model.User;
import com.mnt.sampark.core.db.model.UserDetail;

import java.util.List;

@Repository("UserDetailRepository")
public interface UserDetailRepository extends CrudRepository<UserDetail, String> {

    public UserDetail findByUser(User user);

    @Query(value = "select * from user_detail u where u.id =:id limit 1", nativeQuery = true)
    public UserDetail findByUserDetailId(@Param("id") Long Id);

    @Query(value = "select * from user_detail u where u.user_id =:userId limit 1", nativeQuery = true)
    public UserDetail findByUserId(@Param("userId") Long userId);

    @Query(value = "select COUNT(*) from user_detail u inner join user us on us.id = u.user_id where us.type = 'dept user' and u.last_login < NOW() AND u.last_login > NOW() - INTERVAL 7 DAY and u.state_assembly_id = :state_id", nativeQuery = true)
    public Long findActiveMember(@Param("state_id") Long state_id);

    @Query(value = "select COUNT(*) from user_detail u inner join user us on us.id = u.user_id where us.type = 'dept user' and u.last_login < NOW() AND u.last_login > NOW() - INTERVAL 7 DAY and u.ward_id in :wardIds", nativeQuery = true)
    public Long findActiveMemberByWardIn(@Param("wardIds") List<Long> wardIds);

    @Query(value = "select COUNT(*) from user_detail u inner join user us on us.id = u.user_id where us.type = 'dept user' and u.last_login < NOW() AND u.last_login > NOW() - INTERVAL 7 DAY and u.district_id = (select p.id from parliamentary_constituency p where p.id = :pcId)", nativeQuery = true)
    public Long findActiveMemberByPC(@Param("pcId") Long pcId);

    @Query(value = "select COUNT(*) from user_detail u inner join user us on us.id = u.user_id where us.type = 'dept user' and u.district_id = (select p.id from parliamentary_constituency p where p.id = :pcId)", nativeQuery = true)
    public Long findAllMemberByPC(@Param("pcId") Long pcId);

    @Query(value = "select COUNT(*) from user_detail u inner join user us on us.id = u.user_id where us.type = 'dept user' and u.assembly_constituency_id = :assemblyId", nativeQuery = true)
    public Long findAllMemberByAssembly(@Param("assemblyId") Long assemblyId);

    @Query(value = "select COUNT(*) from user_detail u inner join user us on us.id = u.user_id where us.type = 'dept user' and u.assembly_constituency_id = :assemblyId and u.last_login < NOW() AND u.last_login > NOW() - INTERVAL 7 DAY ", nativeQuery = true)
    public Long findActiveMemberByAssembly(@Param("assemblyId") Long assemblyId);

    @Query(value = "select COUNT(*) from user_detail u inner join user us on us.id = u.user_id where us.type = 'dept user' and u.state_assembly_id = :state_id", nativeQuery = true)
    public Long findAllMember(@Param("state_id") Long state_id);

    @Query(value = "select COUNT(*) from user_detail u inner join user us on us.id = u.user_id where us.type = 'dept user' and u.ward_id in :wardIds ", nativeQuery = true)
    public Long findAllMemberByWardIn(@Param("wardIds") List<Long> userId);

    @Query(value = "select * from user_detail UUD where UUD.state_assembly_id = (select UD.state_assembly_id from user_detail UD where UD.user_id = :userId limit 1)", nativeQuery = true)
    public List<UserDetail> findAllByUserStateId(@Param("userId") Long id);

    public UserDetail findBySubDepartmentIdAndStateAssemblyId(Long subDeptId, Long stateAssemblyId);

    public List<UserDetail> findAllByWardIdAndSubDepartmentId(Long id, Long id2);

    @Query(value = "select UD.state_assembly_id from user_detail UD where UD.user_id = :userId limit 1", nativeQuery = true)
    public Long findStateByUserId(@Param("userId") Long id);
}
