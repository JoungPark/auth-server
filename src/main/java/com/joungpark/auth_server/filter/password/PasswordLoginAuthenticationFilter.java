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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	Environment env;

	private AuthenticationManager authenticationManager;

	public JWTAuthenticationFilter(AuthenticationManager authenticationManager, Environment env) {
		this.authenticationManager = authenticationManager;
		this.env = env;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException {
		System.out.println("JWTAuthenticationFilter ");

		String username = "";
		String password = "";

		try {
			username = req.getParameter("username");
			password = req.getParameter("password");
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
		System.out.println("JWTAuthenticationFilter " + username + "  " + password);

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

		String accessToken = Jwts.builder()
			.setSubject(((User) auth.getPrincipal()).getUsername())
			.setExpiration(new Date(System.currentTimeMillis() + expirationTime))
			.signWith(SignatureAlgorithm.HS512, secretkey.getBytes()).compact();

			
		Token token = new Token();
		token.setAccessToken(accessToken);
		token.setTokenType(tokenPrefix);
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(token);
		
		res.setStatus(HttpServletResponse.SC_OK);

		// res.getWriter().write(tokenPrefix + accessToken);
		res.getWriter().write(json);
		res.getWriter().flush();
		// res.getWriter().close();
		// res.addHeader(headerString, tokenPrefix + token);

		super.successfulAuthentication(req, res, chain, auth);
	}

	class Token {
		private String accessToken;
		private long expiresIn;
		private String jti;
		private String refreshToken;
		private String scope;
		private String tokenType;
		
		public String getAccessToken() {
			return accessToken;
		}
		public void setAccessToken(String accessToken) {
			this.accessToken = accessToken;
		}
		public long getExpiresIn() {
			return expiresIn;
		}
		public void setExpiresIn(long expiresIn) {
			this.expiresIn = expiresIn;
		}
		public String getJti() {
			return jti;
		}
		public void setJti(String jti) {
			this.jti = jti;
		}
		public String getRefreshToken() {
			return refreshToken;
		}
		public void setRefreshToken(String refreshToken) {
			this.refreshToken = refreshToken;
		}
		public String getScope() {
			return scope;
		}
		public void setScope(String scope) {
			this.scope = scope;
		}
		public String getTokenType() {
			return tokenType;
		}
		public void setTokenType(String tokenType) {
			this.tokenType = tokenType;
		}
	}
}