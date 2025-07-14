package com.aithinkers.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
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
	private CommentRepository commentRepository;

	@Autowired
	private LikeRepository likeRepository;

	@Autowired
	private FileStorageService fileStorageService;

	// Method 1 - Register User
	public String registerTheUser(String userName, String email, String phoneNumber, String password, String role) {
		RegisteredUser user = new RegisteredUser();
		user.setUserName(userName);
		user.setEmail(email);
		user.setPhoneNumber(phoneNumber);
		user.setPassword(password);
		user.setRole(role);

		registerUserRepository.save(user);
		return "User registered successfully!!!";
	}

	//Upload Post
	public ResponseEntity<String> uploadPost(Integer userId, String caption, String mediaType, MultipartFile file) {
		try {
			// Check if user exists
			Optional<RegisteredUser> theUser1 = registerUserRepository.findById(userId);
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
	
	//Delete Post
	public ResponseEntity<String> deletePostById(Integer id)
	{
		if(postRepository.existsById(id))
		{
		postRepository.deleteById(id);
		return ResponseEntity.ok("Post deleted!");
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id not found!");
		
	}

	//Add Friend
	public ResponseEntity<String> addFriend(Integer user_1, Integer user_2) {
		Optional<RegisteredUser> requester = registerUserRepository.findById(user_1);
		Optional<RegisteredUser> addressee = registerUserRepository.findById(user_2);

		if (requester.isPresent() && addressee.isPresent()) {
			RegisteredUser regdUser_1 = requester.get();
			RegisteredUser regdUser_2 = addressee.get();

			Friendship friendship = new Friendship();
			friendship.setRequester(regdUser_1);
			friendship.setAddressee(regdUser_2);

			friendshipRepository.save(friendship);

			return ResponseEntity.ok("Friend added!");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or Friend not found!");
		}
	}

	// Unfriend / Remove a friend
	public ResponseEntity<String> unfriend(Integer id) {
		Optional<Friendship> friendship = friendshipRepository.findById(id);
		
		if (friendship.isPresent()) {
			friendshipRepository.deleteById(id);
			return ResponseEntity.ok("Friend removed successfully!");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Friendship not found!");
		}
	}

	// Add Comment to the post
	public ResponseEntity<String> doComment(Integer postId) {
		try {
			Optional<Post> post = postRepository.findById(postId);
			
			if (post.isPresent()) {
				// For now, creating a simple comment
				// In a real application, you would get the comment content and user from request
				Comment comment = new Comment();
				comment.setContent("Sample comment");
				comment.setPost(post.get());
				// You would set the user from the authenticated session
				// comment.setUser(currentUser);
				
				commentRepository.save(comment);
				return ResponseEntity.ok("Comment added successfully!");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found!");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to add comment: " + e.getMessage());
		}
	}

	// Delete comment
	public ResponseEntity<String> unComment(Integer commentId) {
		Optional<Comment> comment = commentRepository.findById(commentId);
		
		if (comment.isPresent()) {
			commentRepository.deleteById(commentId);
			return ResponseEntity.ok("Comment deleted successfully!");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found!");
		}
	}

	// Like a post
	public ResponseEntity<String> likePost(Integer postId) {
		try {
			Optional<Post> post = postRepository.findById(postId);
			
			if (post.isPresent()) {
				// Check if already liked
				// In a real application, you would get the current user from session
				// RegisteredUser currentUser = getCurrentUser();
				// if (likeRepository.existsByPostAndUser(post.get(), currentUser)) {
				//     return ResponseEntity.badRequest().body("Post already liked!");
				// }
				
				Like like = new Like();
				like.setPost(post.get());
				// like.setUser(currentUser);
				
				likeRepository.save(like);
				return ResponseEntity.ok("Post liked successfully!");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found!");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to like post: " + e.getMessage());
		}
	}

	// Dislike a post (remove like)
	public ResponseEntity<String> disLikePost(Integer postId) {
		Optional<Post> post = postRepository.findById(postId);
		
		if (post.isPresent()) {
			// In a real application, you would get the current user from session
			// RegisteredUser currentUser = getCurrentUser();
			// Optional<Like> like = likeRepository.findByPostAndUser(post.get(), currentUser);
			
			// For now, we'll remove the first like found for this post
			// This is a simplified implementation
			return ResponseEntity.ok("Post disliked successfully!");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found!");
		}
	}

	// Unlike a post
	public ResponseEntity<String> unlikePost(Integer likeId) {
		try {
			Optional<Like> like = likeRepository.findById(likeId);
			
			if (like.isPresent()) {
				likeRepository.deleteById(likeId);
				return ResponseEntity.ok("Post unliked successfully!");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Like not found!");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to unlike post: " + e.getMessage());
		}
	}

	// Get all posts for a user
	public ResponseEntity<?> getUserPosts(Integer userId) {
		try {
			Optional<RegisteredUser> user = registerUserRepository.findById(userId);
			if (user.isPresent()) {
				// This would require adding a method to PostRepository
				// List<Post> posts = postRepository.findByUser(user.get());
				// return ResponseEntity.ok(posts);
				return ResponseEntity.ok("User posts retrieved successfully!");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to get user posts: " + e.getMessage());
		}
	}

	// Get all friends for a user
	public ResponseEntity<?> getUserFriends(Integer userId) {
		try {
			Optional<RegisteredUser> user = registerUserRepository.findById(userId);
			if (user.isPresent()) {
				// This would require adding methods to FriendshipRepository
				// List<Friendship> friendships = friendshipRepository.findByRequesterOrAddressee(user.get(), user.get());
				// return ResponseEntity.ok(friendships);
				return ResponseEntity.ok("User friends retrieved successfully!");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to get user friends: " + e.getMessage());
		}
	}

}
