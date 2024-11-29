package com.RBAC.Project.services;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.RBAC.Project.Repository.RoleRepository;
import com.RBAC.Project.Repository.UserRepository;
import com.RBAC.Project.entity.Role;
import com.RBAC.Project.entity.User;

@Service
public class UserServices {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleServices roleServices;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public User registerUser(String username, String password, Set<Role> roles) {
		if (userRepository.findByUsername(username).isPresent()) {
			throw new RuntimeException("User Already Exists");
		}

		User user = new User();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));

		// Ensure roles exist in the database
		Set<Role> existingRoles = new HashSet<>();
		for (Role role : roles) {
			Optional<Role> existingRole = roleServices.findByName(role.getName());
			if (existingRole.isPresent()) {
				existingRoles.add(existingRole.get());
			} else {
				// Save new role if it doesn't exist
				existingRoles.add(roleRepository.save(role));
			}
		}

		user.setRoles(existingRoles);
		return userRepository.save(user);
	}

	public Optional<User> findByUsername(String username) {
		return userRepository.findByUsername(username);
	}
}
