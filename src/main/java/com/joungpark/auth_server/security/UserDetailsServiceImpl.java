package com.joungpark.auth_server.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.joungpark.auth_server.persistence.UserAccount;
import com.joungpark.auth_server.persistence.UserAccountRepository;

import static java.util.Collections.emptyList;

import java.util.Arrays;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private UserAccountRepository userAccountRepository;

	public UserDetailsServiceImpl(UserAccountRepository userAccountRepository) {
		this.userAccountRepository = userAccountRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserAccount appUser = userAccountRepository.findByLoginUserName(username);
		if (appUser == null) {
			throw new UsernameNotFoundException(username);
		}
		
		// @TODO 
		GrantedAuthority authority = new SimpleGrantedAuthority("USER");
		
		// return new User(appUser.getLoginUserName(), appUser.getPassword(), emptyList());
		return new User(appUser.getLoginUserName(), appUser.getPassword(), Arrays.asList(authority));
	}
}