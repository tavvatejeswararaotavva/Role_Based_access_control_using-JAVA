package com.RBAC.Project.services;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.RBAC.Project.Repository.UserRepository;
import com.RBAC.Project.entity.Role;
import com.RBAC.Project.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.expiration}")
	private long expirationTime;
	@Autowired
	private UserRepository userRepository;
	private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

	public String generateToken(User user) {
		Key key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());
		String token = Jwts.builder().setSubject(user.getUsername())
				.claim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
				.setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + expirationTime))
				.signWith(key, SignatureAlgorithm.HS512).compact();
		logger.info("Generated Token: {}", token); // Use logger for better logging
		return token;
	}

	public Claims parseToken(String token) {
		try {
			Key key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8),
					SignatureAlgorithm.HS512.getJcaName());
			logger.info("Validating Token: {}", token);
			Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

			// Optional: Check for expiration manually
			if (claims.getExpiration().before(new Date())) {
				throw new RuntimeException("Token is expired");
			}

			logger.info("Parsed Claims: {}", claims);
			return claims;
		} catch (JwtException | IllegalArgumentException e) {
			logger.error("Invalid JWT Token: {}", e.getMessage());
			throw new RuntimeException("Invalid JWT Token", e);
		}
	}

	public boolean validateToken(String token) {
		Claims claims = parseToken(token);

		// Check if the user exists and is active
		String username = claims.getSubject();
		Optional<User> user = userRepository.findByUsername(username);

		if (user.isPresent()) {
			User existingUser = user.get();

			// Check if the user is active (add 'isActive' field to your User entity)
			if (!existingUser.isActive()) {
				logger.warn("User is deactivated: {}", username);
				throw new RuntimeException("User is deactivated");
			}

			return true; // Token is valid and user is active
		} else {
			logger.warn("User not found: {}", username);
			throw new RuntimeException("User not found");
		}
	}

}