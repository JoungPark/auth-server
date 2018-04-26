package com.joungpark.auth_server.filter.password;

import io.jsonwebtoken.Jwts;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class PasswordLoginAuthorizationFilter extends BasicAuthenticationFilter {

	Environment env;

	public PasswordLoginAuthorizationFilter(AuthenticationManager authManager, Environment env) {
		super(authManager);
		this.env = env;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req,	HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

		String headerString = env.getProperty("jwt-token.headerString");
		String tokenPrefix = env.getProperty("jwt-token.tokenPrefix");

		String header = req.getHeader(headerString);

		if (header == null || !header.startsWith(tokenPrefix)) {
			chain.doFilter(req, res);
			return;
		}

		UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

		SecurityContextHolder.getContext().setAuthentication(authentication);
		chain.doFilter(req, res);
	}

	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
		
		String secretkey = env.getProperty("jwt-token.secretkey");
		String headerString = env.getProperty("jwt-token.headerString");
		String tokenPrefix = env.getProperty("jwt-token.tokenPrefix");

		String token = request.getHeader(headerString);
		if (token != null) {
			// parse the token.
			String user = Jwts.parser()
					.setSigningKey(secretkey.getBytes())
					.parseClaimsJws(token.replace(tokenPrefix, ""))
					.getBody()
					.getSubject();

			if (user != null) {
				return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
			}
			return null;
		}
		return null;
	}
}