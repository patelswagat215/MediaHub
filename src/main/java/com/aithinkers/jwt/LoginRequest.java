package com.aithinkers.jwt;

public class LoginRequest {
	// A simple POJO (Plain Old Java Object) that represents the login credentials sent by the client.
    private String username;

    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}