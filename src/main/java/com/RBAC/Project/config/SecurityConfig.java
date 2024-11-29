package com.RBAC.Project.config;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import io.jsonwebtoken.SignatureAlgorithm;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Value("${jwt.secret}")
	private String secretKey;

	@Bean
	JwtDecoder jwtDecoder() {
		// Create a secret key from the secretKey string
		SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS512.getJcaName());

		// Return the JwtDecoder using the secret key
		return NimbusJwtDecoder.withSecretKey(key).build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth.requestMatchers("/auth/**").permitAll()
						.requestMatchers("/secure/admin/**").hasAuthority("ADMIN").requestMatchers("/secure/user/**")
						.hasAuthority("USER").anyRequest().authenticated())
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder())));

		return http.build();
	}
}