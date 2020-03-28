package com.mnt.sampark.modules.mdm.db.repository;

import com.mnt.sampark.modules.mdm.db.domain.Booth;
import com.mnt.sampark.modules.mdm.db.domain.Ward;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("BoothRepository")
public interface BoothRepository extends CrudJpaSpecRepository<Booth, Long> {

    Optional<Booth> findById(Long Id);

    @Query(value = "SELECT * FROM booth b INNER JOIN ward w ON b.ward_id = w.id INNER JOIN assembly_constituency a ON w.assembly_constituency_id = a.id WHERE a.no =:assemblyNumber AND b.no =:boothNumber limit 1", nativeQuery = true)
    public Booth findByAssemblyNumberAndBoothNo(@Param("assemblyNumber") String assemblyNumber, @Param("boothNumber") String boothNumber);

    public List<Booth> findAllByWard(Ward ward);

    @Query(value = "select * from booth B where B.ward_id = :id and B.no =:no limit 1", nativeQuery = true)
    public Booth customFind(@Param("id") Long id, @Param("no") String no);

    @Query(value = "select * from booth b where b.ward_id = :id order by LPAD(b.name,(SELECT MAX(LENGTH(bt.name)) from booth bt), 0) asc", nativeQuery = true)
    public List<Booth> findAllByWardId(@Param("id") Long id);

    List<Booth> findAll(org.springframework.data.domain.Pageable pageable);

    @Override
    @Query(value = "select * from booth b order by b.name asc", nativeQuery = true)
    List<Booth> findAll();

    @Query(value = "SELECT * FROM booth b INNER JOIN ward w ON b.ward_id = w.id INNER JOIN assembly_constituency a ON w.assembly_constituency_id = a.id"
            + " WHERE b.no =:boothNo AND w.no =:wardNo AND a.no =:assembly_constituency_no limit 1", nativeQuery = true)
    Booth findByBoothNumberAndWardNoAndAssemblyConstituencyNo(@Param("boothNo") String boothNo, @Param("wardNo") String wardNo, @Param("assembly_constituency_no") String assembly_constituency_no);

    @Query(value = "select * from booth b where b.ward_id IN (select w.id from ward w where w.assembly_constituency_id = :assemblyConstituencyId) ", nativeQuery = true)
    public List<Booth> findAllByAssemblyConstituencyId(@Param("assemblyConstituencyId") Long assemblyConstituencyId);

    @Query(value = "SELECT * FROM booth b "
            + "INNER JOIN ward w ON b.ward_id = w.id "
            + "INNER JOIN assembly_constituency a ON w.assembly_constituency_id = a.id "
            + "INNER JOIN parliamentary_constituency p ON p.id = a.parliamentary_constituency_id "
            + "INNER JOIN district d ON d.id = p.district_id "
            + "INNER JOIN state_assembly s ON s.id = d.state_assembly_id "
            + "WHERE b.no = ?1 AND w.no = ?2 AND a.no = ?3 AND s.id = ?4  limit 1", nativeQuery = true)
    public Booth findByBoothNumberAndWardNoAndAssemblyConstituencyNoAndState(String string, String string0, String string1, Long id);

}
