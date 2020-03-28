package com.mnt.sampark.modules.mdm.db.repository;

import com.mnt.sampark.modules.mdm.db.domain.ApplicationSettings;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository("ApplicationSettingsRepository")
public interface ApplicationSettingsRepository extends CrudJpaSpecRepository<ApplicationSettings, Long> {

    public List<ApplicationSettings> findAll();
}
