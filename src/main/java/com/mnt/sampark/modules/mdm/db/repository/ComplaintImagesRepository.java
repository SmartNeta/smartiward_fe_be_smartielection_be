package com.mnt.sampark.modules.mdm.db.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mnt.sampark.modules.mdm.db.domain.ComplaintImages;

@Repository("ComplaintImagesRepository")
public interface ComplaintImagesRepository extends CrudJpaSpecRepository<ComplaintImages, Long> {

    List<ComplaintImages> findByComplaintId(@Param("complaintId") Long complaintId);

}
