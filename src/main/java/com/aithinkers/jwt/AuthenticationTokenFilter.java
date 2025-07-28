package com.aithinkers.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * This filter is executed once per request and is responsible for handling JWT-based authentication.
 * It:
 * - Extracts the JWT token from the request header
 * - Validates the token
 * - Loads the user details
 * - Sets the Spring Security context with the authenticated user
 * 
 * This ensures that all secured endpoints can recognize the user making the request.
 */
public class AuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationTokenFilter.class);

    public AuthenticationTokenFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain filterChain) throws ServletException, IOException {

        logger.debug("AuthenticationTokenFilter triggered for URI: {}", request.getRequestURI());

        try {
            // Extract the JWT token from the Authorization header
            String jwt = parseJwt(request);

            // Validate the token and set authentication context
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {

                // Extract username from token
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                // Load user details from the configured UserDetailsService
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Create an authentication token with the user's details and authorities
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                logger.debug("Authenticated user '{}', roles: {}", username, userDetails.getAuthorities());

                // Set additional request details and update the security context
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (Exception e) {
            logger.error("Failed to set user authentication in security context: {}", e.getMessage());
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Helper method to extract the JWT token from the request header.
     * 
     * @param request the incoming HTTP request
     * @return the JWT token string, or null if not present
     */
    private String parseJwt(HttpServletRequest request) {
        String jwt = jwtUtils.getJwtFromHeader(request);
        logger.debug("Extracted JWT: {}", jwt);
        return jwt;
    }
}
