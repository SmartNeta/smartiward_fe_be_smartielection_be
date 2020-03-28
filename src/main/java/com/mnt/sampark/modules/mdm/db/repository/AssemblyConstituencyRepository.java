package com.mnt.sampark.modules.mdm.db.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mnt.sampark.modules.mdm.db.domain.AssemblyConstituency;
import java.util.List;
import java.util.Optional;

@Repository("AssemblyConstituencyRepository")
public interface AssemblyConstituencyRepository extends CrudJpaSpecRepository<AssemblyConstituency, Long> {

    Optional<AssemblyConstituency> findById(Long assemblyConstituencyId);

    @Query(value = "select * from assembly_constituency A where A.parliamentary_constituency_id = :id and A.no = :no limit 1", nativeQuery = true)
    AssemblyConstituency customFind(@Param("id") Long id, @Param("no") String no);

    @Query(value = "select * from assembly_constituency a where a.parliamentary_constituency_id =:id order by a.name asc", nativeQuery = true)
    public List<AssemblyConstituency> findAllByParliamentaryConstituencyId(@Param("id") Long id);

    @Override
    @Query(value = "select * from assembly_constituency a order by a.name asc", nativeQuery = true)
    public List<AssemblyConstituency> findAll();

    public List<AssemblyConstituency> findAll(org.springframework.data.domain.Pageable pageable);

    @Query(value = "select * from assembly_constituency A "
            + "where A.parliamentary_constituency_id IN (select P.id from parliamentary_constituency P "
            + "where P.district_id IN (select D.id from district D where D.state_assembly_id =:stateId)) order by A.name asc", nativeQuery = true)
    public List<AssemblyConstituency> findByState(@Param("stateId") Long stateId);

    @Query(value = "select a.id from assembly_constituency a where a.parliamentary_constituency_id =:id order by a.name asc", nativeQuery = true)
    public List<Long> findIdsAllByParliamentaryConstituencyId(@Param("id") Long id);

}
