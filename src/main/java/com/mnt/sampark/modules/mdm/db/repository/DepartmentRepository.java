package com.mnt.sampark.modules.mdm.db.repository;

import org.springframework.stereotype.Repository;

import com.mnt.sampark.modules.mdm.db.domain.Department;

@Repository("DepartmentRepository")
public interface DepartmentRepository extends CrudJpaSpecRepository<Department, Long>{

}
