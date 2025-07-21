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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aithinkers.dto.LoginRequest;
import com.aithinkers.dto.LoginResponse;
import com.aithinkers.entity.Friendship;
import com.aithinkers.entity.Post;
import com.aithinkers.jwt.JwtUtils;
import com.aithinkers.repo.FriendshipRepository;
import com.aithinkers.repo.PostRepository;
import com.aithinkers.service.FileStorageService;
import com.aithinkers.service.MediaHubService;

@RestController
@RequestMapping("/mediaHub")
public class MediaHubRest {

	@Autowired
	MediaHubService mediaHubService;

	// Upload a post
	@PostMapping("/uploadThePost")
	public ResponseEntity<String> uploadPost(@RequestParam Integer userId, @RequestParam String caption,
			@RequestParam String mediaType, @RequestParam MultipartFile file) {

		return mediaHubService.uploadPost(userId, caption, mediaType, file);
	}
	
	// Delete a post
	@PostMapping("/delete/{id}")
	public ResponseEntity<String> deletePostById(@PathVariable Integer id) {
		return mediaHubService.deletePostById(id);
	}

	// Add a friend
	@PostMapping("/addFriend")
	public ResponseEntity<String> addFriend(@RequestParam Integer userId_1, @RequestParam Integer userId_2) {

		return mediaHubService.addFriend(userId_1, userId_2);
	}

	// UnFriend /Remove a friend
	@PostMapping("/unfriend/{id}")
	public ResponseEntity<String> unfriend(@PathVariable Integer id) {
		return mediaHubService.unfriend(id);
	}

	// Add Comment to the post
	@PostMapping("/comment/{id}")
	public ResponseEntity<String> doComment(@PathVariable Integer id) {
		return mediaHubService.doComment(id);
	}

	// Delete comment
	@PostMapping("/uncomment/{id}")
	public ResponseEntity<String> unComment(@PathVariable Integer id) {
		return mediaHubService.unComment(id);
	}

	// Like a post
	@PostMapping("/like/{id}")
	public ResponseEntity<String> likePost(@PathVariable Integer id) {
		return mediaHubService.likePost(id);
	}

	// Dislike a post
	@PostMapping("/dislike/{id}")
	public ResponseEntity<String> disLikePost(@PathVariable Integer id) {
		return mediaHubService.disLikePost(id);
	}

	// Unlike a post
	@PostMapping("/unlike/{id}")
	public ResponseEntity<String> unlikePost(@PathVariable Integer id) {
		return mediaHubService.unlikePost(id);
	}

	// Get all posts for a user
	@GetMapping("/user/{userId}/posts")
	public ResponseEntity<?> getUserPosts(@PathVariable Integer userId) {
		return mediaHubService.getUserPosts(userId);
	}

	// Get all friends for a user
	@GetMapping("/user/{userId}/friends")
	public ResponseEntity<?> getUserFriends(@PathVariable Integer userId) {
		return mediaHubService.getUserFriends(userId);
	}

}
