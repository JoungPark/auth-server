package com.joungpark.auth_server.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joungpark.auth_server.persistence.SocialAccountInfo;
import com.joungpark.auth_server.persistence.UserAccount;
import com.joungpark.auth_server.persistence.UserAccountRepository;

@RestController
@RequestMapping("/users")
public class UserController {

	private UserAccountRepository userAccountRepository;
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public UserController(UserAccountRepository userRepository,
			BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userAccountRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@PostMapping("/sign-up")
	public void signUp(@RequestBody PasswordUser passwordUser) {
		System.out.println("signUp " + passwordUser);

		UserAccount userAccount = new UserAccount();
		
		userAccount.setUserId(System.currentTimeMillis());
		userAccount.setLoginUserName(passwordUser.getEmail());
		userAccount.setPassword(bCryptPasswordEncoder.encode(passwordUser.getPassword()));
		userAccount.setDisplayName(passwordUser.getName());

		userAccountRepository.save(userAccount);
	}

	@PostMapping("/sign-up-social")
	public void signUpWithSocial(@RequestBody SocialUser socialUser) {
		System.out.println("signUp social " + socialUser);

		UserAccount userAccount = new UserAccount();
		
		userAccount.setUserId(System.currentTimeMillis());
		userAccount.setLoginUserName(socialUser.getId());
		userAccount.setPassword(bCryptPasswordEncoder.encode(socialUser.getId()));
		userAccount.setDisplayName(socialUser.getName());
		
		SocialAccountInfo socialAccountInfo = new SocialAccountInfo();
		socialAccountInfo.setEmail(socialUser.getEmail());
		socialAccountInfo.setName(socialUser.getName());
		socialAccountInfo.setProvider(socialUser.getProvider());
		socialAccountInfo.setSocialId(socialUser.getId());
		
		socialAccountInfo.setUserAccount(userAccount);
		userAccount.setSocialAccountInfo(socialAccountInfo);
		
		try {
			userAccountRepository.save(userAccount);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}