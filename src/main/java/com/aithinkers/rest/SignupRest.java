package com.aithinkers.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.aithinkers.dto.RegisterUser;
import com.aithinkers.jwt.JwtUtils;
import com.aithinkers.service.MediaHubService;

@RestController
public class SignupRest {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	MediaHubService mediaHubService;

	//Register new User into the database.
	@PostMapping("/register")
	public String registerUser(@RequestBody RegisterUser registerUser) {

		return mediaHubService.registerTheUser(registerUser.getUserName(), registerUser.getEmail(), registerUser.getPhoneNumber(), registerUser.getPassword(),registerUser.getRole());
	}

	// SignIn Existing User ang generate the JWT Token, Password= MediaHub@123
	@PostMapping("/signIn")
	public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
		Authentication authentication;
		
		try {
			authentication = authenticationManager.
			authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
			
		} catch (AuthenticationException exception) {
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

		LoginResponse response = new LoginResponse(userDetails.getUsername(), roles, jwtToken);

		return ResponseEntity.ok(response);
	}

}
