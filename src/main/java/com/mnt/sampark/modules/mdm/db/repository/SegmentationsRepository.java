package com.mnt.sampark.modules.mdm.db.repository;

import com.mnt.sampark.modules.mdm.db.domain.Segmentations;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("SegmentationsRepository")
public interface SegmentationsRepository extends CrudJpaSpecRepository<Segmentations, Long> {

    Segmentations findByStateAssemblyIdAndSegmentation(Long stateId, String action);

    List<Segmentations> findAllByStateAssemblyIdAndVisibilityOrderBySequenceAsc(@Param("stateId") Long stateId, @Param("visibility") boolean b);

    @Query(value = "select s.label from segmentations s where s.state_assembly_id = :stateId and s.visibility is true order by s.sequence asc", nativeQuery = true)
    List<String> findAllLabelsByStateAssemblyIdAndVisibilityOrderBySequenceAsc(@Param("stateId") Long stateId);

    @Query(value = "select s.label, s.segmentation from segmentations s where s.state_assembly_id = :stateId and s.visibility is true order by s.sequence asc", nativeQuery = true)
    List<Object[]> findAllLabelsAndSegmentationByStateAssemblyIdAndVisibilityOrderBySequenceAsc(@Param("stateId") Long stateId);

    @Query(value = "select s.label from segmentations s where s.state_assembly_id = :stateId and s.segmentation = :segmentation limit 1", nativeQuery = true)
    public String findLabelBySegmentationAndStateAssemblyId(@Param("segmentation") String segmentation, @Param("stateId") String stateId);

}
