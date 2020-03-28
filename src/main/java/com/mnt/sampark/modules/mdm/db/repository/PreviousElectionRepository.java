/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mnt.sampark.modules.mdm.db.repository;

import com.mnt.sampark.modules.mdm.db.domain.ParliamentaryConstituency;
import com.mnt.sampark.modules.mdm.db.domain.Party;
import com.mnt.sampark.modules.mdm.db.domain.PreviousElection;
import java.io.Serializable;
import org.springframework.stereotype.Repository;

/**
 *
 * @author govind
 */
@Repository("PreviousElectionRepository")
public interface PreviousElectionRepository extends CrudJpaSpecRepository<PreviousElection, Serializable> {

    PreviousElection findByBoothNumberAndWardNumberAndAssemblyNumberAndPartyAndYearAndParliamentaryConstituency(String boothNumber, String wardNumber, String assemblyNumber, Party party, Long year, ParliamentaryConstituency parliamentaryConstituency);

}
