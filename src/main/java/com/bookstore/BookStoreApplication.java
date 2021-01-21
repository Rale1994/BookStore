package com.bookstore;

import com.bookstore.domain.User;
import com.bookstore.security.Role;
import com.bookstore.security.UserRole;
import com.bookstore.service.UserService;
import com.bookstore.utility.SecurityUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class BookStoreApplication implements CommandLineRunner {
	@Autowired
	private UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(BookStoreApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		User user= new User();
		user.setFirstName("John");
		user.setLastName("Adams");
		user.setUsername("ja");
		user.setPassword(SecurityUtility.passwordEncoder().encode("p"));
		user.setEmail("jadams@gmail.com");
		Set<UserRole> userRoles= new HashSet<>();
		Role role1= new Role();
		role1.setRoleId(1);
		role1.setName("ROLE_USER");
		userRoles.add(new UserRole(user, role1));

		userService.createUser(user, userRoles);

	}
}
