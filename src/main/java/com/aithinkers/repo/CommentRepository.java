package com.aithinkers.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aithinkers.entity.Comment;
import com.aithinkers.entity.Post;
import com.aithinkers.entity.RegisteredUser;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByPost(Post post);
    Optional<Comment> findByPostAndUser(Post post, RegisteredUser user);
} 