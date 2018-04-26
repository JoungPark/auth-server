package com.joungpark.auth_server.persistence;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class UserAccount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="user_account_id")
	private long id;

	@Column(unique=true)
	private String username;
	private String password;
	
	@OneToOne(mappedBy = "userAccount", cascade=CascadeType.ALL)
	public SocialAccountInfo socialAccountInfo;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

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

	public SocialAccountInfo getSocialAccountInfo() {
		return socialAccountInfo;
	}

	public void setSocialAccountInfo(SocialAccountInfo socialAccountInfo) {
		this.socialAccountInfo = socialAccountInfo;
	}
	
	
}