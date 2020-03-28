package com.mnt.sampark.modules.mdm.tools;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import com.mnt.sampark.core.db.model.LongIdBaseEntity;
import com.mnt.sampark.modules.mdm.db.repository.CrudJpaSpecRepository;

@Component
public class MdmFactory {

    @Autowired
    private ApplicationContext context;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public LongIdBaseEntity save(LongIdBaseEntity domain, Class<? extends LongIdBaseEntity> domainClass) {
        CrudRepository repositoryIns = getRepository(domainClass);
        return (LongIdBaseEntity) repositoryIns.save(domain);
    }

    public LongIdBaseEntity findById(Class<? extends LongIdBaseEntity> domainClass, Long id) {
        CrudRepository<?, Long> repositoryIns = getRepository(domainClass);
		return (LongIdBaseEntity)repositoryIns.findById(id).get();
    }

    public List<LongIdBaseEntity> query(Class<? extends LongIdBaseEntity> domainClass, Specification< LongIdBaseEntity> spec) {
        CrudJpaSpecRepository<LongIdBaseEntity, Long> repositoryIns = getJpaSpecRepository(domainClass);
        List<LongIdBaseEntity> list = new ArrayList<>();
        repositoryIns.findAll(spec).forEach(list::add);
        return list;
    }

    public List<LongIdBaseEntity> findAll(Class<? extends LongIdBaseEntity> domainClass) {
        CrudRepository<LongIdBaseEntity, Long> repositoryIns = getRepository(domainClass);
        List<LongIdBaseEntity> list = new ArrayList<>();
        repositoryIns.findAll().forEach(list::add);
        return list;
    }

    public List<LongIdBaseEntity> findAll(String classType) {
        Class<? extends LongIdBaseEntity> domainClass;
        try {
            domainClass = (Class<? extends LongIdBaseEntity>) Class.forName("com.mnt.sampark.modules.mdm.db.domain." + classType);
            CrudRepository<LongIdBaseEntity, Long> repositoryIns = getRepository(domainClass);
            List<LongIdBaseEntity> list = new ArrayList<>();
            repositoryIns.findAll().forEach(list::add);
            return list;
        } catch (ClassNotFoundException e) {
            //TODO
            // Delegate classType to some other factory like
            // return somefactory.get(classType);
            return null;
        }
    }

    public boolean isIdPresent(Class<? extends LongIdBaseEntity> domainClass, Long id) {
        CrudRepository<?, Long> repositoryIns = getRepository(domainClass);
		return repositoryIns.findById(id).isPresent();
    }

    private CrudRepository<LongIdBaseEntity, Long> getRepository(Class<? extends LongIdBaseEntity> domainClass) {
        String repository = domainClass.getSimpleName() + "Repository";
        @SuppressWarnings("unchecked")
        CrudRepository<LongIdBaseEntity, Long> repositoryIns = (CrudRepository<LongIdBaseEntity, Long>) context.getBean(repository);
        return repositoryIns;
    }

    private CrudJpaSpecRepository<LongIdBaseEntity, Long> getJpaSpecRepository(Class<? extends LongIdBaseEntity> domainClass) {
        String repository = domainClass.getSimpleName() + "Repository";
        @SuppressWarnings("unchecked")
        CrudJpaSpecRepository<LongIdBaseEntity, Long> repositoryIns = (CrudJpaSpecRepository<LongIdBaseEntity, Long>) context.getBean(repository);
        return repositoryIns;
    }

    public void delete(Class<? extends LongIdBaseEntity> domainClass, Long id) {
        CrudRepository<?, Long> repositoryIns = getRepository(domainClass);
        repositoryIns.deleteById(id);
    }

}
