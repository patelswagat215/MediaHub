package com.aithinkers.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aithinkers.entity.Friendship;
import com.aithinkers.entity.Post;
import com.aithinkers.entity.RegisterUser;
import com.aithinkers.jwt.JwtUtils;
import com.aithinkers.jwt.LoginRequest;
import com.aithinkers.jwt.LoginResponse;
import com.aithinkers.repo.FriendshipRepository;
import com.aithinkers.repo.RegisterUserRepository;
import com.aithinkers.repo.PostRepository;
import com.aithinkers.service.FileStorageService;
import com.aithinkers.service.MediaHubService;

@RestController
@RequestMapping("/mediaHub")
public class MediaHubRest {

	@Autowired
	MediaHubService mediaHubService;

	// Register New User
	@PostMapping("/registerTheUser")
	public String registerUser(@RequestParam String userName, @RequestParam String email,@RequestParam String phoneNumber, @RequestParam String password) {

		return mediaHubService.registerTheUser(userName, email, phoneNumber, password);
	}
	

	// Login the user
	@PostMapping("/loginTheUser")
	public ResponseEntity<String> loginExistingUser(@RequestParam String userName, @RequestParam String password) {
		
		return mediaHubService.loginTheUser(userName, password);
	}
	

	// Upload a post
	@PostMapping("/uploadThePost")
	public ResponseEntity<String> uploadPost(@RequestParam Integer userId, @RequestParam String caption,@RequestParam String mediaType, @RequestParam MultipartFile file) {

		return mediaHubService.uploadPost(userId, caption, mediaType, file);
	}
	

	// Add friend
	@PostMapping("/addFriend")
	public ResponseEntity<String> addFriend(@RequestParam Integer userId_1, @RequestParam Integer userId_2) {
		
		return mediaHubService.addFriend(userId_1, userId_2);
	}

}
