package com.mnt.sampark.core.db.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mnt.sampark.core.db.model.User;

@Repository("UserRepository")
public interface UserRepository extends CrudRepository<User, String> {

    User findByUsername(String username);

}
