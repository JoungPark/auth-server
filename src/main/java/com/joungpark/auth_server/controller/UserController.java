package com.joungpark.auth_server.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}