package com.RBAC.Project.contollers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/secure")
public class SecureController {
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/admin")
	public String adminAccess() {
		return "Welcome Admin!";
	}

	@PreAuthorize("hasAuthority('USER')")
	@GetMapping("/user")
	public String userAccess() {
		return "Welcome User!";
	}
}