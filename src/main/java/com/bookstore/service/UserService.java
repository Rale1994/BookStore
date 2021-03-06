package com.bookstore.service;

import com.bookstore.domain.User;
import com.bookstore.security.PasswordResetToken;
import com.bookstore.security.UserRole;

import java.util.Set;

public interface UserService {
    PasswordResetToken getPasswordResetToken(final String token);

    void createPasswordResetTokenForUser(final User user, final String token);

    User findByUsername(String username);

    User findByEmail(String email);

    User createUser(User user, Set<UserRole> userRoles) throws Exception;

    User saveUser(User user);
}
