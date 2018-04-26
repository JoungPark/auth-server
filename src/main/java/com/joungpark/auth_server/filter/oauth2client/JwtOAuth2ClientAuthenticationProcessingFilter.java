package com.joungpark.auth_server.filter.oauth2client;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.joungpark.auth_server.filter.AuthInfo;

import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtOAuth2ClientAuthenticationProcessingFilter extends OAuth2ClientAuthenticationProcessingFilter {

	Environment env;
	
	public JwtOAuth2ClientAuthenticationProcessingFilter(String defaultFilterProcessesUrl, Environment env) {
		super(defaultFilterProcessesUrl);
		this.env = env;
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest req,
			HttpServletResponse res,
			FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		
		Long expirationTime = Long.parseLong(env.getProperty("jwt-token.expirationTime"));
		String secretkey = env.getProperty("jwt-token.secretkey");
		// String headerString = env.getProperty("jwt-token.headerString");
		String tokenPrefix = env.getProperty("jwt-token.tokenPrefix");

		// System.out.println("successfulAuthentication" + auth);
		// String token = Jwts.builder()
		// 		// .setSubject(((User) auth.getPrincipal()).getUsername())
		// 		.setSubject((String)auth.getPrincipal())
		// 		.setExpiration(new Date(System.currentTimeMillis() + expirationTime))
		// 		.signWith(SignatureAlgorithm.HS512, secretkey.getBytes())
		// 		.compact();
		// res.addHeader(headerString, tokenPrefix + token);

		String accessToken = Jwts.builder()
			.setSubject((String) auth.getPrincipal())
			.setExpiration(new Date(System.currentTimeMillis() + expirationTime))
			.signWith(SignatureAlgorithm.HS512, secretkey.getBytes()).compact();
		
		AuthInfo authInfo = new AuthInfo();
		authInfo.setAccessToken(accessToken);
		authInfo.setTokenType(tokenPrefix);
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(authInfo);

		res.setStatus(HttpServletResponse.SC_OK);

		res.getWriter().write(json);
		res.getWriter().flush();

		super.successfulAuthentication(req, res, chain, auth);
	}
}

