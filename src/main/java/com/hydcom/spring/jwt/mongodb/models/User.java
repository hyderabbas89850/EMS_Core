package com.hydcom.spring.jwt.mongodb.models;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Document(collection = "users")
public class User {

	@Id
	private String id;

	private String userId;

	@NotBlank
	@Size(max = 50)
	@Email
	private String email;
	
	private String username;
	
	@NotBlank
	@Size(max = 120)
	private String password;
	
	private String firstName;
	private String lastName;
	private String mobile;
	private String address;

	@DBRef
	private Set<Role> roles = new HashSet<>();

	public User() {
	}

	public User(String email,String username, String password, String firstName, String lastName, String mobile, String address) {
		this.firstName = firstName;
		this.address = address;
		this.lastName = lastName;
		this.mobile = mobile;
		this.username = username;
		this.email = email;
		this.password = password;
	}
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
