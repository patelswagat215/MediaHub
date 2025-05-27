package com.aithinkers.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 🔁 Dummy hardcoded user for demo. Replace with DB logic later.
        if ("admin".equals(username)) {
            return User.withUsername("admin")
                       .password("{noop}admin123") // {noop} for plain text password
                       .roles("ADMIN")
                       .build();
        } else if ("user".equals(username)) {
            return User.withUsername("user")
                       .password("{noop}user123")
                       .roles("USER")
                       .build();
        }

        throw new UsernameNotFoundException("User not found: " + username);
    }
}

