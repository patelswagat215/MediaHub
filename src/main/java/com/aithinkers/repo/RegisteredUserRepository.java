package com.aithinkers.repo;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import com.aithinkers.entity.RegisteredUser;


public interface RegisteredUserRepository extends JpaRepository<RegisteredUser, Integer> {

	  Optional<RegisteredUser> findByUserName(String userName);
	  Optional<RegisteredUser> findByEmail(String email);
	  Optional<RegisteredUser> findByPhoneNumber(String phoneNumber);
	  
	  
}
