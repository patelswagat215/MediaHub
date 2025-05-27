package com.aithinkers.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aithinkers.entity.Friendship;

public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {}
