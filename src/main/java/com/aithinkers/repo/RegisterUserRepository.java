package com.aithinkers.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aithinkers.entity.RegisteredUser;


public interface RegisterUserRepository extends JpaRepository<RegisteredUser, Integer> {

	  Optional<RegisteredUser> findByUserName(String userName);
}
