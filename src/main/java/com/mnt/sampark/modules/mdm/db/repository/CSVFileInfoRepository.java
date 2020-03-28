package com.mnt.sampark.modules.mdm.db.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.mnt.sampark.modules.mdm.db.domain.CSVFileInfo;
import java.util.Date;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository("CSVFileInfoRepository")
public interface CSVFileInfoRepository extends CrudJpaSpecRepository<CSVFileInfo, Long> {

    Optional<CSVFileInfo> findById(Long fileId);

    CSVFileInfo findByStatus(String status);

    List<CSVFileInfo> findAllByStatus(String status);

    List<CSVFileInfo> findAllByState(String state);

    List<CSVFileInfo> findAllByStateOrderByStatusDesc(String state);

    @Query(value = "select * from csv_file_info c where c.modifieddate <= :creationDateTime", nativeQuery = true)
    List<CSVFileInfo> findAllWithCreationDateTimeBefore(@Param("creationDateTime") Date creationDateTime);
}
