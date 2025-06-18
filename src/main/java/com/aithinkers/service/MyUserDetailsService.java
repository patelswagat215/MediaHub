package com.aithinkers.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.aithinkers.entity.RegisterUser;
import com.aithinkers.repo.RegisterUserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private RegisterUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	
        RegisterUser user = userRepository
        						.findByUserName(username)
        						.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new UserPrincipal(user); 
    }
}
