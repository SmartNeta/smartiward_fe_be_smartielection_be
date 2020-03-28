package com.mnt.sampark.modules.mdm.db.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mnt.sampark.modules.mdm.db.domain.ParliamentaryConstituency;
import java.util.List;
import java.util.Optional;

@Repository("ParliamentaryConstituencyRepository")
public interface ParliamentaryConstituencyRepository extends CrudJpaSpecRepository<ParliamentaryConstituency, Long> {

    Optional<ParliamentaryConstituency> findById(Long parliamentaryConstituencyId);

    @Query(value = "select * from parliamentary_constituency P where P.district_id = :id and P.no = :no limit 1", nativeQuery = true)
    ParliamentaryConstituency customFind(@Param("id") Long id, @Param("no") String no);

    @Query(value = "select * from parliamentary_constituency p where p.district_id = :id order by p.name asc", nativeQuery = true)
    public List<ParliamentaryConstituency> findAllByDistrictId(@Param("id") Long id);

    @Query(value = "select * from parliamentary_constituency P where P.district_id IN (select D.id from district D where D.state_assembly_id = :state_id) order by P.name asc", nativeQuery = true)
    public List<ParliamentaryConstituency> findAllByState(@Param("state_id") Long state_id);

    @Override
    @Query(value = "select * from parliamentary_constituency p order by p.name asc", nativeQuery = true)
    public List<ParliamentaryConstituency> findAll();

}
