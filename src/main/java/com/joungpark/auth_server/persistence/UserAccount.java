package com.joungpark.auth_server.persistence;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class UserAccount {
	@Id
	@Column(nullable = false, name = "user_id")
	private long userId;

	@Column(nullable = false, unique = true)
	private String loginUserName;
	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String displayName;

	@OneToOne(mappedBy = "userAccount", cascade = CascadeType.ALL)
	public SocialAccountInfo socialAccountInfo;

	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getLoginUserName() {
		return loginUserName;
	}
	public void setLoginUserName(String loginUserName) {
		this.loginUserName = loginUserName;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public SocialAccountInfo getSocialAccountInfo() {
		return socialAccountInfo;
	}

	public void setSocialAccountInfo(SocialAccountInfo socialAccountInfo) {
		this.socialAccountInfo = socialAccountInfo;
	}


}