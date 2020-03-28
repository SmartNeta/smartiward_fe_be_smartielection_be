package com.mnt.sampark.modules.mdm.db.repository;

import com.mnt.sampark.modules.mdm.db.domain.Party;
import com.mnt.sampark.modules.mdm.db.domain.StateAssembly;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("PartyRepository")
public interface PartyRepository extends CrudJpaSpecRepository<Party, Long> {

    List<Party> findAllByStateAssembly(StateAssembly stateAssembly);

    @Query(value = "SELECT * FROM party P where (P.state_assembly_id =:said AND P.district_id IS NULL) OR (P.district_id =:did AND P.assembly_constituency_id IS NULL )OR(P.assembly_constituency_id =:acid AND P.ward_id IS NULL ) oR P.ward_id =:wid ORDER BY P.code ASC", nativeQuery = true)
    List<Party> partyPreference(@Param("said") Long said, @Param("did") Long did, @Param("acid") Long acid, @Param("wid") Long wid);

    @Query(value = "SELECT * FROM party P where (P.state_assembly_id =:said AND P.district_id IS NULL) OR (P.district_id =:did AND P.assembly_constituency_id IS NULL )OR(P.assembly_constituency_id =:acid) ORDER BY P.code ASC", nativeQuery = true)
    List<Party> partyPreferenceByAssembly(@Param("said") Long said, @Param("did") Long did, @Param("acid") Long acid);

    @Query(value = "SELECT * FROM party P where (P.state_assembly_id =:said AND P.district_id IS NULL) OR (P.district_id =:did) ORDER BY P.code ASC", nativeQuery = true)
    List<Party> partyPreferenceByParliamentary(@Param("said") Long said, @Param("did") Long did);

    @Query(value = "SELECT * FROM party P WHERE (P.state_assembly_id =:said AND P.district_id IS NULL) OR (P.district_id =:did AND P.assembly_constituency_id IS NULL )OR(P.assembly_constituency_id =:acid) ORDER BY P.code ASC", nativeQuery = true)
    List<Party> partyPreference(@Param("said") Long said, @Param("did") Long did, @Param("acid") Long acid);

    @Query(value = "SELECT * FROM party p WHERE p.code = :code AND p.ward_id = :wardId limit 1;", nativeQuery = true)
    Party findByCodeAndWardId(@Param("code") String partyCode, @Param("wardId") Long wardId);

    @Query(value = "SELECT * FROM party p WHERE p.code = :code AND p.assembly_constituency_id = :assemblyId limit 1;", nativeQuery = true)
    Party findByCodeAndAssemblyId(@Param("code") String partyCode, @Param("assemblyId") Long assemblyId);

    @Query(value = "SELECT * FROM party p WHERE p.code = :code AND p.state_assembly_id = :stateId limit 1;", nativeQuery = true)
    Party findByCodeAndStateId(@Param("code") String partyCode, @Param("stateId") Long stateId);

}
