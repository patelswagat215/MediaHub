package com.aithinkers.rest;

import java.awt.print.Pageable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aithinkers.dto.LoginRequest;
import com.aithinkers.dto.LoginResponse;
import com.aithinkers.dto.RegisterUserRequest;
import com.aithinkers.dto.UploadPostRequest;
import com.aithinkers.entity.Friendship;
import com.aithinkers.entity.Post;
import com.aithinkers.entity.RegisteredUser;
import com.aithinkers.jwt.JwtUtils;
import com.aithinkers.repo.FriendshipRepository;
import com.aithinkers.repo.PostRepository;
import com.aithinkers.service.FileStorageService;
import com.aithinkers.service.MediaHubService;

import jakarta.validation.Valid;
/**
* REST controller for MediaHub features including user management, posting, 
* friendship, commenting, and likes/dislikes.
*/
@RestController
@RequestMapping("/mediaHub")
public class MediaHubRest {

	@Autowired
	MediaHubService mediaHubService;

	/**
	 * Retrieves all registered users.
	 * Endpoint: GET /mediaHub/getAllUsers
	 * 
	 * @return List of all RegisteredUser entities
	 */
	@GetMapping("/getAllUsers")
	public ResponseEntity<List<RegisteredUser>> getAllUsers() {
		List<RegisteredUser> allUsers = mediaHubService.getAllUsers();
		return ResponseEntity.ok(allUsers);
	}

	/**
	 * Retrieves all registered users sorted by username in ascending order.
	 * Endpoint: GET /mediaHub/getAllUsers/sorted-by-username
	 * 
	 * @return List of sorted RegisteredUser entities
	 */
	@GetMapping("/getAllUsers/sorted-by-username")
	public ResponseEntity<List<RegisteredUser>> getSortedUsersByUserName() {
		List<RegisteredUser> sortedUsers = mediaHubService.getSortedUsers();
		return ResponseEntity.ok(sortedUsers);
	}

	/**
	 * Retrieves a paginated list of users.
	 * Endpoint: GET /mediaHub/getPaginatedUsers
	 * 
	 * @param from Start index
	 * @param to   End index
	 * @return List of paginated RegisteredUser entities
	 */
	@GetMapping("/getPaginatedUsers")
	public ResponseEntity<List<RegisteredUser>> getAllUsers(@RequestParam Integer from, @RequestParam Integer to) {
		List<RegisteredUser> users = mediaHubService.findEmp(from, to);
		return ResponseEntity.ok(users);
	}
	
	/**
	 * Updates a user's details by ID.
	 * Endpoint: PUT /mediaHub/updateUserNameById/{Id}
	 * 
	 * @param Id ID of the user to update
	 * @param registerUser New user details
	 * @return ResponseEntity with status and message
	 */
	@PutMapping("/updateUserNameById/{Id}")
	public ResponseEntity<?> updateUserById(@PathVariable Integer Id, @RequestBody RegisterUserRequest registerUser) {
		return mediaHubService.updateUserById(Id, registerUser);
	}

	/**
	 * Deletes a user by ID.
	 * Endpoint: DELETE /mediaHub/deleteUserById/{Id}
	 * 
	 * @param Id ID of the user to delete
	 * @return ResponseEntity with status and message
	 */
	@DeleteMapping("/deleteUserById/{Id}")
	public ResponseEntity<String> deleteUserById(@PathVariable Integer Id) {
		return mediaHubService.deleteUserById(Id);
	}

	/**
	 * Uploads a post with media.
	 * Endpoint: POST /mediaHub/uploadThePost
	 * 
	 * @param request UploadPostRequest with userId, caption, mediaType, and file
	 * @return ResponseEntity with status and message
	 */
	@PostMapping("/uploadThePost")
	public ResponseEntity<String> uploadPost(@Valid @ModelAttribute UploadPostRequest request) {
		return mediaHubService.uploadPost(request.getUserId(), request.getCaption(), request.getMediaType(), request.getFile());
	}

	/**
	 * Deletes a post by its ID.
	 * Endpoint: DELETE /mediaHub/delete/{id}
	 * 
	 * @param id ID of the post to delete
	 * @return ResponseEntity with status and message
	 */
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deletePostById(@PathVariable Integer id) {
		return mediaHubService.deletePostById(id);
	}

	/**
	 * Adds a friend relationship between two users.
	 * Endpoint: POST /mediaHub/addFriend
	 * 
	 * @param userId_1 ID of the first user
	 * @param userId_2 ID of the second user
	 * @return ResponseEntity with status and message
	 */
	@PostMapping("/addFriend")
	public ResponseEntity<String> addFriend(@RequestParam Integer userId_1, @RequestParam Integer userId_2) {
		return mediaHubService.addFriend(userId_1, userId_2);
	}

	/**
	 * Removes a friend relationship by friendship ID.
	 * Endpoint: DELETE /mediaHub/unfriend/{id}
	 * 
	 * @param id Friendship ID to remove
	 * @return ResponseEntity with status and message
	 */
	@DeleteMapping("/unfriend/{id}")
	public ResponseEntity<String> unfriend(@PathVariable Integer id) {
		return mediaHubService.unfriend(id);
	}

	/**
	 * Adds a comment to a post.
	 * Endpoint: POST /mediaHub/comment/{id}
	 * 
	 * @param id ID of the post
	 * @return ResponseEntity with status and message
	 */
	@PostMapping("/comment/{id}")
	public ResponseEntity<String> doComment(@PathVariable Integer id) {
		return mediaHubService.doComment(id);
	}

	/**
	 * Deletes a comment by its ID.
	 * Endpoint: DELETE /mediaHub/uncomment/{id}
	 * 
	 * @param id ID of the comment
	 * @return ResponseEntity with status and message
	 */
	@DeleteMapping("/uncomment/{id}")
	public ResponseEntity<String> unComment(@PathVariable Integer id) {
		return mediaHubService.unComment(id);
	}

	/**
	 * Likes a post by its ID.
	 * Endpoint: POST /mediaHub/like/{id}
	 * 
	 * @param id ID of the post
	 * @return ResponseEntity with status and message
	 */
	@PostMapping("/like/{id}")
	public ResponseEntity<String> likePost(@PathVariable Integer id) {
		return mediaHubService.likePost(id);
	}

	/**
	 * Dislikes a post by its ID.
	 * Endpoint: POST /mediaHub/dislike/{id}
	 * 
	 * @param id ID of the post
	 * @return ResponseEntity with status and message
	 */
	@PostMapping("/dislike/{id}")
	public ResponseEntity<String> disLikePost(@PathVariable Integer id) {
		return mediaHubService.disLikePost(id);
	}

	/**
	 * Unlikes a post by its ID.
	 * Endpoint: DELETE /mediaHub/unlike/{id}
	 * 
	 * @param id ID of the post
	 * @return ResponseEntity with status and message
	 */
	@DeleteMapping("/unlike/{id}")
	public ResponseEntity<String> unlikePost(@PathVariable Integer id) {
		return mediaHubService.unlikePost(id);
	}

	/**
	 * Retrieves all posts of a specific user.
	 * Endpoint: GET /mediaHub/user/{userId}/posts
	 * 
	 * @param userId ID of the user
	 * @return ResponseEntity containing list of posts
	 */
	@GetMapping("/user/{userId}/posts")
	public ResponseEntity<?> getUserPosts(@PathVariable Integer userId) {
		return mediaHubService.getUserPosts(userId);
	}

	/**
	 * Retrieves all friends of a specific user.
	 * Endpoint: GET /mediaHub/user/{userId}/friends
	 * 
	 * @param userId ID of the user
	 * @return ResponseEntity containing list of friends
	 */
	@GetMapping("/user/{userId}/friends")
	public ResponseEntity<?> getUserFriends(@PathVariable Integer userId) {
		return mediaHubService.getUserFriends(userId);
	}
}
