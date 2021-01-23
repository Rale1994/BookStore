package com.adminportal.Adminportal.service;

import com.adminportal.Adminportal.domain.User;
import com.adminportal.Adminportal.security.PasswordResetToken;
import com.adminportal.Adminportal.security.UserRole;

import java.util.Set;

public interface UserService {


    User createUser(User user, Set<UserRole> userRoles) throws Exception;

    User saveUser(User user);
}
