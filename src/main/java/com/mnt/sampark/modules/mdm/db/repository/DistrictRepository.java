package com.mnt.sampark.modules.mdm.db.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mnt.sampark.modules.mdm.db.domain.District;
import java.util.List;

@Repository("DistrictRepository")
public interface DistrictRepository extends CrudJpaSpecRepository<District, Long> {

    @Query(value = "select * from district D where D.state_assembly_id = :id and D.no = :no limit 1", nativeQuery = true)
    District customFind(@Param("id") Long id, @Param("no") String no);

    public List<District> findAllByStateAssemblyId(Long id);

}
