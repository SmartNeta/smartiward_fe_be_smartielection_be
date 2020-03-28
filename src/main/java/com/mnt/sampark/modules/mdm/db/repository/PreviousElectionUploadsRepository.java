/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mnt.sampark.modules.mdm.db.repository;

import com.mnt.sampark.modules.mdm.db.domain.PreviousElectionUploads;
import java.io.Serializable;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author govind
 */
@Repository("PreviousElectionUploadsRepository")
public interface PreviousElectionUploadsRepository extends CrudJpaSpecRepository<PreviousElectionUploads, Serializable> {

    public List<PreviousElectionUploads> findAllByWardId(Long wardId);

    public List<PreviousElectionUploads> findAllByAssemblyConstituencyId(Long assemblyConstituencyId);

}
