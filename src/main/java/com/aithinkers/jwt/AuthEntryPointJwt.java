package com.aithinkers.jwt;
/*
 * AuthEntryPointJwt
→ Provides custom handling for
unauthorized requests, typically
when authentication is required
but not supplied or valid.
→When an unauthorized
request is detected, it logs the
error and returns a JSON
response with an error message,
status code, and the path
attempted.*/
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
                         throws IOException, ServletException {

        System.err.println("Unauthorized error: " + authException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }
}
