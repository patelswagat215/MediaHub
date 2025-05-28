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

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtils jwtUtils;

	// Testing Get
	@GetMapping("/getMessage")
	public String getMsg() {
		return "Hello Media Hub";
	}

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

	// Add friend
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

	// SignIn
    @PostMapping("/signIn")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        LoginResponse response = new LoginResponse(userDetails.getUsername(), roles, jwtToken);

        return ResponseEntity.ok(response);
    }
}
