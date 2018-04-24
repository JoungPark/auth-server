package com.joungpark.auth_server.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuthClientController {
	@RequestMapping({ "/user", "/me" })
	public Principal user(Principal principal) {
		System.out.println("user " + principal);
		return principal;
	}
}
