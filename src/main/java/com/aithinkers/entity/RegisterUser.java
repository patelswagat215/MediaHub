package com.aithinkers.entity;

import org.springframework.stereotype.Repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "registered_user")
@Data
public class RegisterUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "email")
	private String email;

	@Column(name = "phone_number")
	private String phoneNumber;
	
	@Column(name="password")
	private String password;

}
