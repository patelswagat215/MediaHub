package com.aithinkers.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "user_posts")
@Data
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name="caption")
	private String caption;

	@Column(name="media_type")
	private String mediaType;

	@Column(name="media_url")
	private String mediaUrl;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private RegisterUser user;
}
