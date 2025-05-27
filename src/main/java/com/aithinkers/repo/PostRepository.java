package com.aithinkers.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aithinkers.entity.Post;

public interface PostRepository extends JpaRepository<Post, Integer> {}

