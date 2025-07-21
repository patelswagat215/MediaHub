package com.aithinkers.dto;

import lombok.Data;

@Data
public class RegisterUserRequest {
	private String userName;
	private String email;
	private String phoneNumber;
	private String password;
	private String role;

}
