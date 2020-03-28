package com.mnt.sampark.modules.mdm.db.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CrudJpaSpecRepository<T, ID extends Serializable> extends CrudRepository<T, ID>, JpaSpecificationExecutor<T> {

    //public Optional<T> findById(Long Id);
}
