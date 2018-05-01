package com.joungpark.auth_server;

import com.joungpark.auth_server.filter.oauth2client.OAuth2ClientAuthenticationManager;
import com.joungpark.auth_server.filter.password.PasswordLoginAuthenticationFilter;
import com.joungpark.auth_server.filter.password.PasswordLoginAuthorizationFilter;
import com.joungpark.auth_server.filter.social.SocialLoginAuthenticationFilter;
import com.joungpark.auth_server.persistence.UserAccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {
	@Autowired
	OAuth2ClientAuthenticationManager oAuth2ClientAuthenticationManager;

	@Autowired
	Environment env;
	
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private UserAccountRepository userAccountRepository;
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// String signupPath = env.getProperty("signup.path");
		// @formatter:off
		http
		.authorizeRequests()
		.antMatchers("/login").permitAll()
		.antMatchers("/oauth/token/revokeById/**").permitAll()
		.antMatchers("/tokens/**").permitAll()
		.antMatchers("/").permitAll()
		.antMatchers("/auth/**").permitAll()
		.antMatchers("/login/**").permitAll()
		.antMatchers("/users/**").permitAll()
		.anyRequest().authenticated()
		.and()
			.formLogin().permitAll()
		.and()
			.logout()
			.logoutUrl("/logout")
			.logoutSuccessUrl("/").permitAll()
		.and()
			.csrf().disable();
		// @formatter:on

		http
		// .addFilter(new PasswordLoginAuthenticationFilter(authenticationManager(), env, userAccountRepository))
		// .addFilter(new PasswordLoginAuthorizationFilter(authenticationManager(), env))
		.addFilterAfter(new SocialLoginAuthenticationFilter("/login/social/facebook", "POST", authenticationManager(), env, userAccountRepository), UsernamePasswordAuthenticationFilter.class)
		// .addFilterBefore(new JwtAuthenticationProcessingFilter("Authorization"), UsernamePasswordAuthenticationFilter.class)
		.addFilterAfter(oAuth2ClientAuthenticationManager.getJwtFacebookFilter("/login/facebook", env),	UsernamePasswordAuthenticationFilter.class)
		;
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		super.configure(web);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// super.configure(auth);		// must make it remarked
		// auth.inMemoryAuthentication().withUser("foo").password("1").roles("USER");
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
	}

	@Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}