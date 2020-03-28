package com.mnt.sampark.modules.mdm.db.repository;

import com.mnt.sampark.modules.mdm.db.domain.Logo;
import org.springframework.stereotype.Repository;

@Repository("LogoRepository")
public interface LogoRepository extends CrudJpaSpecRepository<Logo, Long>{
}
