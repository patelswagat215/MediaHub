package com.aithinkers.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aithinkers.entity.RegisterUser;


public interface RegisterUserRepository extends JpaRepository<RegisterUser, Integer> {

	  Optional<RegisterUser> findByUserName(String userName);
}
