package com.mnt.sampark.modules.mdm.db.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.mnt.sampark.modules.mdm.db.domain.NewsFeed;
import java.util.Optional;

@Repository("NewsFeedRepository")
public interface NewsFeedRepository extends CrudJpaSpecRepository<NewsFeed, Long> {

    public List<NewsFeed> findAllByStateAssemblyIdOrderByCreatedDateDesc(Long id);

    public Optional<NewsFeed> findById(Long newsId);

}
