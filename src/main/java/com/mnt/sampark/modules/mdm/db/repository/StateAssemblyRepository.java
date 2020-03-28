package com.mnt.sampark.modules.mdm.db.repository;

import org.springframework.stereotype.Repository;

import com.mnt.sampark.modules.mdm.db.domain.StateAssembly;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

@Repository("StateAssemblyRepository")
public interface StateAssemblyRepository extends CrudJpaSpecRepository<StateAssembly, Long> {

    public Optional<StateAssembly> findById(Long stateId);

    StateAssembly findByState(String state);

    @Override
    @Query(value = "select * from state_assembly s order by s.state asc", nativeQuery = true)
    List<StateAssembly> findAll();

}
