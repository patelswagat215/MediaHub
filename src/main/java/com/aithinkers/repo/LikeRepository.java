package com.aithinkers.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aithinkers.entity.Like;
import com.aithinkers.entity.Post;
import com.aithinkers.entity.RegisteredUser;

public interface LikeRepository extends JpaRepository<Like, Integer> {
    Optional<Like> findByPostAndUser(Post post, RegisteredUser user);
    boolean existsByPostAndUser(Post post, RegisteredUser user);
} 