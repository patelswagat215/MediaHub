package com.aithinkers.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aithinkers.dto.LoginRequest;
import com.aithinkers.dto.LoginResponse;
import com.aithinkers.dto.RegisterUserRequest;
import com.aithinkers.jwt.JwtUtils;
import com.aithinkers.service.MediaHubService;

/**
 * REST controller for user registration and authentication endpoints.
 */
@RestController
@RequestMapping("/public")
public class SignupController {

	private static final Logger logger = LoggerFactory.getLogger(SignupController.class);

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	MediaHubService mediaHubService;

	/**
	 * Registers a new user. Returns 201 if successful, 409 if duplicate, 500 on error.
	 * @param registerUser the registration request DTO
	 * @return ResponseEntity with status and message
	 */
	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody RegisterUserRequest registerUser) {
		logger.info("Registration attempt for email: {} and phone: {}", registerUser.getEmail(), registerUser.getPhoneNumber());
		try {
			// Hash the plain text password before registration
			registerUser.setPassword(mediaHubService.encodePassword(registerUser.getPassword()));
			String result = mediaHubService.registerUser(registerUser);
			if (result.startsWith("Duplicate user")) {
				logger.warn("Duplicate registration attempt: {}", result);
				return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
			}
			logger.info("User registered successfully: {}", registerUser.getEmail());
			return ResponseEntity.status(HttpStatus.CREATED).body(result);
		} catch (Exception e) {
			logger.error("Registration failed for email: {}: {}", registerUser.getEmail(), e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed: " + e.getMessage());
		}
	}

	/**
	 * Authenticates an existing user and generates a JWT token.
	 * @param loginRequest the login request DTO
	 * @return ResponseEntity with JWT token and user info
	 */
	@PostMapping("/signIn")
	public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
		logger.info("Authentication attempt for username: {}", loginRequest.getUsername());
		Authentication authentication;
		
		try {
			authentication = authenticationManager.
			authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
			
		} catch (AuthenticationException exception) {
			logger.warn("Authentication failed for username: {}", loginRequest.getUsername());
			Map<String, Object> map = new HashMap<>();
			map.put("message", "Bad credentials");
			map.put("status", false);
			return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
		}

		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();

		String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		logger.info("Authentication successful for username: {}", loginRequest.getUsername());
		LoginResponse response = new LoginResponse(userDetails.getUsername(), roles, jwtToken);

		return ResponseEntity.ok(response);
	}

}
