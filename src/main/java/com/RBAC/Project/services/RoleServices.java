package com.RBAC.Project.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.RBAC.Project.Repository.RoleRepository;
import com.RBAC.Project.entity.Role;

@Service
public class RoleServices {
	@Autowired
	private RoleRepository roleRepository;

	public Optional<Role> findByName(String roleName) {
		return roleRepository.findByName(roleName);
	}

	public Role createRole(Role role) {
		if (roleRepository.findByName(role.getName()).isPresent()) {
			throw new RuntimeException("Role already exists");
		}
		return roleRepository.save(role);
	}
}