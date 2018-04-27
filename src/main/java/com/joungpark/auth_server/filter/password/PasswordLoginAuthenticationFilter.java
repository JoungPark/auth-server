package com.joungpark.auth_server.filter.password;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.joungpark.auth_server.filter.AuthInfo;
import com.joungpark.auth_server.persistence.SocialAccountInfo;
import com.joungpark.auth_server.persistence.UserAccount;
import com.joungpark.auth_server.persistence.UserAccountRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class PasswordLoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	Environment env;

	private AuthenticationManager authenticationManager;
	
	private UserAccountRepository userAccountRepository;
	
	public PasswordLoginAuthenticationFilter(AuthenticationManager authenticationManager, Environment env, UserAccountRepository userAccountRepository) {
		this.authenticationManager = authenticationManager;
		this.env = env;
		this.userAccountRepository = userAccountRepository;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException {
		System.out.println("JWTAuthenticationFilter ");

		String username = "";
		String password = "";
		String logintype = "";

		try {
			username = req.getParameter("username");
			password = req.getParameter("password");
			logintype = req.getParameter("logintype");
		} catch (Exception e) {
			System.out.println(e);
		}
		
		// try {
		// 	ApplicationUser creds = new ObjectMapper().readValue(req.getInputStream(), ApplicationUser.class);
		// 	username = creds.getUsername();
		// 	password = creds.getPassword();
		// } catch (Exception e) {
		// 	System.out.println(e);
		// }
		System.out.println("JWTAuthenticationFilter " + username + "  " + password + "  " + logintype);

		if (!username.equals("")) {
			return authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>()));
		} else {
			return null;
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) throws IOException, ServletException {
		Long expirationTime = Long.parseLong(env.getProperty("jwt-token.expirationTime"));
		String secretkey = env.getProperty("jwt-token.secretkey");
//		String headerString = env.getProperty("jwt-token.headerString");
		String tokenPrefix = env.getProperty("jwt-token.tokenPrefix");

		String username = ((User) auth.getPrincipal()).getUsername();
		UserAccount userAccount = userAccountRepository.findByUsername(username);
		Long userId = userAccount.getId();
		SocialAccountInfo socialAccountInfo = userAccount.getSocialAccountInfo();
		String displayusername= username;
		if (socialAccountInfo != null) {
			displayusername = userAccount.getSocialAccountInfo().getName();
		}
		System.out.println(userId);
		String accessToken = Jwts.builder()
			.claim("userId", userId)
			.claim("displayusername", displayusername)
			.setSubject(((User) auth.getPrincipal()).getUsername())
			.setExpiration(new Date(System.currentTimeMillis() + expirationTime))
			.signWith(SignatureAlgorithm.HS512, secretkey.getBytes()).compact();
		
		AuthInfo authInfo = new AuthInfo();
		authInfo.setAccessToken(accessToken);
		authInfo.setTokenType(tokenPrefix);
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(authInfo);
		
		res.setStatus(HttpServletResponse.SC_OK);

		// res.getWriter().write(tokenPrefix + accessToken);
		res.getWriter().write(json);
		res.getWriter().flush();
		// res.getWriter().close();
		// res.addHeader(headerString, tokenPrefix + token);

		super.successfulAuthentication(req, res, chain, auth);
	}
}