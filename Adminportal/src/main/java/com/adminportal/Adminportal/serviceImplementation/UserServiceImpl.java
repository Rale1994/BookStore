package com.adminportal.Adminportal.serviceImplementation;

import com.adminportal.Adminportal.repository.RoleRepository;
import com.adminportal.Adminportal.repository.UserRepository;
import com.adminportal.Adminportal.security.UserRole;
import com.adminportal.Adminportal.service.UserService;
import com.adminportal.Adminportal.domain.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public User createUser(User user, Set<UserRole> userRoles) {
        User localUser = userRepository.findByUsername(user.getUsername());

        if (localUser != null) {
            LOG.info("user {} already exist. Nothing will be done.", user.getUsername());
        } else {
            for (UserRole urole : userRoles) {
                roleRepository.save(urole.getRole());
            }
            user.getUserRoles().addAll(userRoles);
            localUser = userRepository.save(user);
        }
        return localUser;
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }


}
