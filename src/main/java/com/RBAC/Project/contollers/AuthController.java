package com.RBAC.Project.contollers;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.RBAC.Project.entity.User;
import com.RBAC.Project.services.JwtService;
import com.RBAC.Project.services.UserServices;

@Controller
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	private UserServices userServices;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtService jwtService;

	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody User request) {

		userServices.registerUser(request.getUsername(), request.getPassword(), request.getRoles());
		return ResponseEntity.ok("User Registered Successfully");
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody User loginRequest) {
		User user = userServices.findByUsername(loginRequest.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
			return ResponseEntity.status(401).body("Invalid credentials");
		}

		String token = jwtService.generateToken(user);
		return ResponseEntity.ok(Collections.singletonMap("token", token));
	}
}
