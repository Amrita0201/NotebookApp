package com.tarento.notebook.models;

import org.springframework.security.core.userdetails.UserDetails;

public class User {
	
	private Long id;
	private String name;
	private String email;
	private String password;
	private Boolean isActive;
	private Boolean isDeleted;
	private String creationDate;
	private String token;
	
	public User() {} 
	public User(UserDetails userDetails) { 
		this.email = userDetails.getUsername();
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public Boolean getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
