package com.mnt.sampark.modules.mdm.db.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mnt.sampark.modules.mdm.db.domain.Ward;

@Repository("WardRepository")
public interface WardRepository extends CrudJpaSpecRepository<Ward, Long> {

    public Optional<Ward> findById(Long wardId);

    @Query(value = "select * from ward W where W.assembly_constituency_id = :id and W.no = :no limit 1", nativeQuery = true)
    public Ward customFind(@Param("id") Long id, @Param("no") String no);

    @Query(value = "select * from ward w where w.assembly_constituency_id = :id order by LPAD(w.name,(SELECT MAX(LENGTH(name)) from ward), 0) asc", nativeQuery = true)
    public List<Ward> findAllByAssemblyConstituencyId(@Param("id") Long id);

    public List<Ward> findAll(org.springframework.data.domain.Pageable pageable);

    @Override
    @Query(value = "select * from ward w order by w.name asc", nativeQuery = true)
    public List<Ward> findAll();

    @Query(value = "select * from ward W where W.assembly_constituency_id  = :assemblyConstituencyId", nativeQuery = true)
    List<Ward> findByAssemblyConstituency(@Param("assemblyConstituencyId") Long assemblyConstituencyId);

    @Query(value = "select W.id from ward W where W.assembly_constituency_id  in :assemblyConstituencyIds", nativeQuery = true)
    List<Long> findWardIdsByAssemblyConstituencyIdsIn(@Param("assemblyConstituencyIds") List<Long> assemblyConstituencyIds);

}
