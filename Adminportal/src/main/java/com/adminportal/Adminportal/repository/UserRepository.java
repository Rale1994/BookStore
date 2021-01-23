package com.adminportal.Adminportal.repository;

import com.adminportal.Adminportal.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository <User, Long> {
    User findByUsername(String username);
}
