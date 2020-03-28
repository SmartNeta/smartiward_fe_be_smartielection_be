package com.mnt.sampark.modules.mdm.db.repository;

import com.mnt.sampark.modules.mdm.db.domain.SubDepartment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("SubDepartmentRepository")
public interface SubDepartmentRepository extends CrudJpaSpecRepository<SubDepartment, Long> {

    public List<SubDepartment> findAllByDepartmentId(Long departmentId);

    @Query(value = "select * from sub_department SD where SD.name = 'Data admin' limit 1", nativeQuery = true)
    public SubDepartment findElectionDept();

    public Optional<SubDepartment> findById(Long id);

}
