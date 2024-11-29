package com.RBAC.Project.contollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RBAC.Project.entity.Role;
import com.RBAC.Project.services.RoleServices;

@RestController
@RequestMapping("/roles")
public class RoleController {
	@Autowired
	private RoleServices roleServices;

	@PostMapping("/create")
	public ResponseEntity<String> createRole(@RequestBody Role role) {
		if (role.getName() == null || role.getName().isEmpty()) {
			return ResponseEntity.badRequest().body("Role name is required");
		}
		roleServices.createRole(role);
		return ResponseEntity.ok("Role created successfully");
	}
}
