package com.aithinkers.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.aithinkers.entity.RegisteredUser;
import com.aithinkers.repo.RegisteredUserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private RegisteredUserRepository registeredUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	
        RegisteredUser user = registeredUserRepository
        						.findByUserName(username)
        						.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new UserPrincipal(user); 
    }
}
