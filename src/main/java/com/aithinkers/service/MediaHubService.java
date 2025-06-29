package com.aithinkers.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.aithinkers.entity.Friendship;
import com.aithinkers.entity.Post;
import com.aithinkers.entity.RegisterUser;
import com.aithinkers.repo.FriendshipRepository;
import com.aithinkers.repo.PostRepository;
import com.aithinkers.repo.RegisterUserRepository;

@Service
public class MediaHubService {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	FriendshipRepository friendshipRepository;

	@Autowired
	private RegisterUserRepository registerUserRepository;

	@Autowired
	private FileStorageService fileStorageService;

	// Method 1 - Register User
	public String registerTheUser(String userName, String email, String phoneNumber, String password) {
		RegisterUser user = new RegisterUser();
		user.setUserName(userName);
		user.setEmail(email);
		user.setPhoneNumber(phoneNumber);
		user.setPassword(password);

		registerUserRepository.save(user);
		return "User registered successfully!!!";
	}

	// Method 2 - Login User
	public ResponseEntity<String> loginTheUser(String userName, String password) {
		Optional<RegisterUser> theUser = registerUserRepository.findByUserName(userName);

		if (theUser.isPresent()) {
			RegisterUser user = theUser.get();
			if (user.getPassword().equals(password)) {
				return ResponseEntity.ok("Login successful!");
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password");
			}
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}
	}

	// Method 3 - Upload Post
	public ResponseEntity<String> uploadPost(Integer userId, String caption, String mediaType, MultipartFile file) {
		try {
			// Check if user exists
			Optional<RegisterUser> theUser1 = registerUserRepository.findById(userId);
			if (!theUser1.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
			}

			// Save file locally
			String filePath = fileStorageService.save(file);

			// Create post
			Post post = new Post();
			post.setCaption(caption);
			post.setMediaType(mediaType);
			post.setMediaUrl(filePath);
			post.setUser(theUser1.get());

			// Save post to DB
			postRepository.save(post);

			return ResponseEntity.ok("Post uploaded successfully.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to upload post: " + e.getMessage());
		}
	}

	// Method 3 - Add Friend
	public ResponseEntity<String> addFriend(Integer user_1, Integer user_2) {
		Optional<RegisterUser> requester = registerUserRepository.findById(user_1);
		Optional<RegisterUser> addressee = registerUserRepository.findById(user_2);

		if (requester.isPresent() && addressee.isPresent()) {
			RegisterUser regdUser_1 = requester.get();
			RegisterUser regdUser_2 = addressee.get();

			Friendship friendship = new Friendship();
			friendship.setRequester(regdUser_1);
			friendship.setAddressee(regdUser_2);

			friendshipRepository.save(friendship);

			return ResponseEntity.ok("Friend added!");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or Friend not found!");
		}
	}

}
