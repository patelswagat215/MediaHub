package com.aithinkers.rest;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aithinkers.entity.Friendship;
import com.aithinkers.entity.Post;
import com.aithinkers.entity.RegisterUser;
import com.aithinkers.repo.FriendshipRepository;
import com.aithinkers.repo.MediaHubRepository;
import com.aithinkers.repo.PostRepository;
import com.aithinkers.service.FileStorageService;

@RestController
@RequestMapping("/mediaHub")
public class MediaHubRest {

	@Autowired
	MediaHubRepository repo;

	@Autowired
	PostRepository postRepo;

	@Autowired
	FileStorageService fileStorageService;

	@Autowired
	FriendshipRepository friendshipRepository;

	// Register New User
	@PostMapping("/registerTheUser")
	public String registerUser(@RequestParam String userName, @RequestParam String email,
			@RequestParam String phoneNumber, @RequestParam String password) {

		RegisterUser user = new RegisterUser();
		user.setUserName(userName);
		user.setEmail(email);
		user.setPhoneNumber(phoneNumber);
		user.setPassword(password);

		repo.save(user);

		return "User registered successfully!!!";
	}

	// Login the user
	@PostMapping("/loginTheUser")
	public ResponseEntity<String> loginExistingUser(@RequestParam String userName, @RequestParam String password) {
		Optional<RegisterUser> theUser = repo.findByUserName(userName);

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

	// Upload a post
	@PostMapping("/uploadThePost")
	public ResponseEntity<String> uploadPost(@RequestParam Integer userId, @RequestParam String caption,
			@RequestParam String mediaType, @RequestParam MultipartFile file) {

		// Save file in local
		String filePath = fileStorageService.save(file);

		Optional<RegisterUser> theUser = repo.findById(userId);
		Post post = new Post();
		post.setCaption(caption);
		post.setMediaType(mediaType);
		post.setMediaUrl(filePath);
		post.setUser(theUser.get());

		postRepo.save(post);

		return ResponseEntity.ok("Post uploaded.");
	}

	//Add friend
	@PostMapping("/addFriend")
	public ResponseEntity<String> addFriend(@RequestParam Integer user_1, @RequestParam Integer user_2) {

		Optional<RegisterUser> requester = repo.findById(user_1);
		Optional<RegisterUser> addressee = repo.findById(user_2);

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

	@GetMapping("/getMessage")
	public String getMsg() {
		return "Hello Media Hub";
	}

}
