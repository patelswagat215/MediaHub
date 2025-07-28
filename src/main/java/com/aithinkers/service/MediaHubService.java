package com.aithinkers.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aithinkers.entity.Comment;
import com.aithinkers.entity.Friendship;
import com.aithinkers.entity.Like;
import com.aithinkers.entity.Post;
import com.aithinkers.entity.RegisteredUser;
import com.aithinkers.repo.CommentRepository;
import com.aithinkers.repo.FriendshipRepository;
import com.aithinkers.repo.LikeRepository;
import com.aithinkers.repo.PostRepository;
import com.aithinkers.repo.RegisteredUserRepository;
import com.aithinkers.dto.RegisterUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service layer for MediaHub business logic, including user registration, posts, friends, comments, and likes.
 */
@Service
public class MediaHubService {

	private static final Logger logger = LoggerFactory.getLogger(MediaHubService.class);

	@Autowired
	private PostRepository postRepository;

	@Autowired
	FriendshipRepository friendshipRepository;

	@Autowired
	private RegisteredUserRepository registeredUserRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private LikeRepository likeRepository;

	@Autowired
	private FileStorageService fileStorageService;

	/**
	 * Updates user information by ID.
	 */
	public ResponseEntity<?> updateUserById(Integer id, RegisterUserRequest request) {
	    logger.info("Updating user with ID: {}", id);
	    RegisteredUser user = registeredUserRepository.findById(id)
	        .orElseThrow(() -> new RuntimeException("User not found"));

	    user.setUserName(request.getUserName());
	    user.setPhoneNumber(request.getPhoneNumber());
	    user.setRole(request.getRole());
	    user.setEmail(request.getEmail());

	    registeredUserRepository.save(user);
	    logger.info("User updated successfully: {}", id);
	    return ResponseEntity.ok("Successfully Updated");
	}

	/**
	 * Retrieves paginated list of users.
	 */
	public List<RegisteredUser> findEmp(Integer from, Integer to) {
		logger.info("Fetching users from page: {}, size: {}", from, to);
		PageRequest pagerequest = PageRequest.of(from, to);
		Page<RegisteredUser> all = registeredUserRepository.findAll(pagerequest);
		return all.getContent();
	}

	/**
	 * Retrieves all users sorted by username.
	 */
	public List<RegisteredUser> getSortedUsers() {
	    logger.info("Fetching all users sorted by username");
	    Sort sort = Sort.by(Sort.Direction.ASC, "userName");
	    return registeredUserRepository.findAll(sort);
	}

	/**
	 * Retrieves all users.
	 */
	public List<RegisteredUser> getAllUsers() {
		logger.info("Fetching all users");
		return registeredUserRepository.findAll();
	}

	/**
	 * Registers a new user if email and phone number are unique.
	 */
	public String registerUser(RegisterUserRequest request) {
		logger.info("Attempting to register user with email: {} and phone: {}", request.getEmail(), request.getPhoneNumber());
		if (registeredUserRepository.findByEmail(request.getEmail()).isPresent()) {
			logger.warn("Duplicate email registration attempt: {}", request.getEmail());
			return "Duplicate user: Email already exists.";
		}
		if (registeredUserRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
			logger.warn("Duplicate phone registration attempt: {}", request.getPhoneNumber());
			return "Duplicate user: Phone number already exists.";
		}
		RegisteredUser user = new RegisteredUser();
		user.setUserName(request.getUserName());
		user.setEmail(request.getEmail());
		user.setPhoneNumber(request.getPhoneNumber());
		user.setPassword(request.getPassword());
		user.setRole(request.getRole());

		registeredUserRepository.save(user);
		logger.info("User registered successfully: {}", request.getEmail());
		return "User registered successfully!!!";
	}

	/**
	 * Uploads a media post for a user.
	 */
	public ResponseEntity<String> uploadPost(Integer userId, String caption, String mediaType, MultipartFile file) {
		logger.info("Uploading post for user ID: {}", userId);
		try {
			Optional<RegisteredUser> theUser = registeredUserRepository.findById(userId);
			if (!theUser.isPresent()) {
				logger.warn("User not found with ID: {}", userId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
			}

			String filePath = fileStorageService.save(file);

			Post post = new Post();
			post.setCaption(caption);
			post.setMediaType(mediaType);
			post.setMediaUrl(filePath);
			post.setUser(theUser.get());

			postRepository.save(post);
			logger.info("Post uploaded successfully for user ID: {}", userId);
			return ResponseEntity.ok("Post uploaded successfully.");
		} catch (Exception e) {
			logger.error("Failed to upload post: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to upload post: " + e.getMessage());
		}
	}

	/**
	 * Deletes a post by ID.
	 */
	public ResponseEntity<String> deletePostById(Integer id) {
		logger.info("Deleting post with ID: {}", id);
		if (postRepository.existsById(id)) {
			postRepository.deleteById(id);
			logger.info("Post deleted with ID: {}", id);
			return ResponseEntity.ok("Post deleted!");
		}
		logger.warn("Post ID not found: {}", id);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id not found!");
	}

	/**
	 * Deletes a user by ID.
	 */
	public ResponseEntity<String> deleteUserById(Integer Id) {
		logger.info("Deleting user with ID: {}", Id);
		if (registeredUserRepository.existsById(Id)) {
			registeredUserRepository.deleteById(Id);
			logger.info("User deleted with ID: {}", Id);
			return ResponseEntity.ok("User deleted!");
		}
		logger.warn("User ID not found: {}", Id);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id not found!");
	}

	/**
	 * Adds a friend connection between two users.
	 */
	public ResponseEntity<String> addFriend(Integer user_1, Integer user_2) {
		logger.info("Adding friend from user {} to user {}", user_1, user_2);
		Optional<RegisteredUser> requester = registeredUserRepository.findById(user_1);
		Optional<RegisteredUser> addressee = registeredUserRepository.findById(user_2);

		if (requester.isPresent() && addressee.isPresent()) {
			RegisteredUser regdUser_1 = requester.get();
			RegisteredUser regdUser_2 = addressee.get();

			Friendship friendship = new Friendship();
			friendship.setRequester(regdUser_1);
			friendship.setAddressee(regdUser_2);

			friendshipRepository.save(friendship);
			logger.info("Friend added between {} and {}", user_1, user_2);
			return ResponseEntity.ok("Friend added!");
		} else {
			logger.warn("User or Friend not found!");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or Friend not found!");
		}
	}

	/**
	 * Removes a friendship by ID.
	 */
	public ResponseEntity<String> unfriend(Integer id) {
		logger.info("Removing friendship with ID: {}", id);
		Optional<Friendship> friendship = friendshipRepository.findById(id);

		if (friendship.isPresent()) {
			friendshipRepository.deleteById(id);
			logger.info("Friendship removed: {}", id);
			return ResponseEntity.ok("Friend removed successfully!");
		} else {
			logger.warn("Friendship not found: {}", id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Friendship not found!");
		}
	}

	/**
	 * Adds a comment to a post.
	 */
	public ResponseEntity<String> doComment(Integer postId) {
		logger.info("Adding comment to post ID: {}", postId);
		try {
			Optional<Post> post = postRepository.findById(postId);

			if (post.isPresent()) {
				Comment comment = new Comment();
				comment.setContent("Sample comment");
				comment.setPost(post.get());

				commentRepository.save(comment);
				logger.info("Comment added to post ID: {}", postId);
				return ResponseEntity.ok("Comment added successfully!");
			} else {
				logger.warn("Post not found: {}", postId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found!");
			}
		} catch (Exception e) {
			logger.error("Failed to add comment: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to add comment: " + e.getMessage());
		}
	}

	/**
	 * Deletes a comment by ID.
	 */
	public ResponseEntity<String> unComment(Integer commentId) {
		logger.info("Deleting comment with ID: {}", commentId);
		Optional<Comment> comment = commentRepository.findById(commentId);

		if (comment.isPresent()) {
			commentRepository.deleteById(commentId);
			logger.info("Comment deleted: {}", commentId);
			return ResponseEntity.ok("Comment deleted successfully!");
		} else {
			logger.warn("Comment not found: {}", commentId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found!");
		}
	}

	/**
	 * Likes a post.
	 */
	public ResponseEntity<String> likePost(Integer postId) {
		logger.info("Liking post with ID: {}", postId);
		try {
			Optional<Post> post = postRepository.findById(postId);

			if (post.isPresent()) {
				Like like = new Like();
				like.setPost(post.get());

				likeRepository.save(like);
				logger.info("Post liked: {}", postId);
				return ResponseEntity.ok("Post liked successfully!");
			} else {
				logger.warn("Post not found: {}", postId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found!");
			}
		} catch (Exception e) {
			logger.error("Failed to like post: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to like post: " + e.getMessage());
		}
	}

	/**
	 * Dislikes a post (placeholder).
	 */
	public ResponseEntity<String> disLikePost(Integer postId) {
		logger.info("Disliking post with ID: {}", postId);
		Optional<Post> post = postRepository.findById(postId);

		if (post.isPresent()) {
			logger.info("Post disliked (placeholder logic): {}", postId);
			return ResponseEntity.ok("Post disliked successfully!");
		} else {
			logger.warn("Post not found: {}", postId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found!");
		}
	}

	/**
	 * Unlikes a post by like ID.
	 */
	public ResponseEntity<String> unlikePost(Integer likeId) {
		logger.info("Unliking post with like ID: {}", likeId);
		try {
			Optional<Like> like = likeRepository.findById(likeId);

			if (like.isPresent()) {
				likeRepository.deleteById(likeId);
				logger.info("Post unliked with like ID: {}", likeId);
				return ResponseEntity.ok("Post unliked successfully!");
			} else {
				logger.warn("Like not found: {}", likeId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Like not found!");
			}
		} catch (Exception e) {
			logger.error("Failed to unlike post: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to unlike post: " + e.getMessage());
		}
	}

	/**
	 * Retrieves all posts by a user.
	 */
	public ResponseEntity<?> getUserPosts(Integer userId) {
		logger.info("Fetching posts for user ID: {}", userId);
		try {
			Optional<RegisteredUser> user = registeredUserRepository.findById(userId);
			if (user.isPresent()) {
				List<Post> posts = postRepository.findAll();
				logger.info("Posts fetched for user ID: {}", userId);
				return ResponseEntity.ok(posts);
			} else {
				logger.warn("User not found: {}", userId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
			}
		} catch (Exception e) {
			logger.error("Failed to get user posts: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to get user posts: " + e.getMessage());
		}
	}

	/**
	 * Retrieves all friends of a user.
	 */
	public ResponseEntity<?> getUserFriends(Integer userId) {
		logger.info("Fetching friends for user ID: {}", userId);
		try {
			Optional<RegisteredUser> user = registeredUserRepository.findById(userId);
			if (user.isPresent()) {
				List<Friendship> friendships = friendshipRepository.findAll();
				logger.info("Friends fetched for user ID: {}", userId);
				return ResponseEntity.ok(friendships);
			} else {
				logger.warn("User not found: {}", userId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
			}
		} catch (Exception e) {
			logger.error("Failed to get user friends: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to get user friends: " + e.getMessage());
		}
	}

	/**
	 * Hashes a plain text password using BCrypt.
	 */
	public String encodePassword(String plainPassword) {
		logger.info("Encoding password using BCrypt");
		org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
		return encoder.encode(plainPassword);
	}
}
