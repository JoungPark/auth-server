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
	public void signUp(@RequestBody UserAccount userAccount) {
		System.out.println("signUp " + userAccount);
		userAccount.setPassword(bCryptPasswordEncoder.encode(userAccount.getPassword()));
		userAccountRepository.save(userAccount);
	}

	@PostMapping("/sign-up-social")
	public void signUpWithSocial(@RequestBody SocialUser socialUser) {
		System.out.println("signUp social " + socialUser);

		UserAccount userAccount = new UserAccount();
		userAccount.setUsername(socialUser.getId());
		userAccount.setPassword(bCryptPasswordEncoder.encode(socialUser.getId()));
		
		SocialAccountInfo socialAccountInfo = new SocialAccountInfo();
		socialAccountInfo.setEmail(socialUser.getEmail());
		socialAccountInfo.setName(socialUser.getName());
		socialAccountInfo.setProvider(socialUser.getProvider());
		socialAccountInfo.setSocialId(socialUser.getId());
		
		socialAccountInfo.setUserAccount(userAccount);
		userAccount.setSocialAccountInfo(socialAccountInfo);
		
		userAccountRepository.save(userAccount);
		
	}
}