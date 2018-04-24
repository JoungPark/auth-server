package com.joungpark.auth_server.filter.oauth2client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

@Configuration
public class OAuth2ClientAuthenticationManager {
	@Autowired
	OAuth2ClientContext oauth2ClientContext;
	
	public OAuth2ClientAuthenticationProcessingFilter getFacebookFilter(String defaultFilterProcessesUrl) {
		ClientResources clientResources = facebook();

		OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(defaultFilterProcessesUrl);
		
		filter.setRestTemplate(getFacebookOAuth2RestTemplate(clientResources));
		filter.setTokenServices(getFacebookUserInfoTokenServices(clientResources));
		
		return filter;
	}
	
	public JwtOAuth2ClientAuthenticationProcessingFilter getJwtFacebookFilter(String defaultFilterProcessesUrl, Environment env) {
		ClientResources clientResources = facebook();

		JwtOAuth2ClientAuthenticationProcessingFilter filter = new JwtOAuth2ClientAuthenticationProcessingFilter(defaultFilterProcessesUrl, env);
		
		filter.setRestTemplate(getFacebookOAuth2RestTemplate(clientResources));
		filter.setTokenServices(getFacebookUserInfoTokenServices(clientResources));
		
		return filter;
	}
	
	private OAuth2RestTemplate getFacebookOAuth2RestTemplate(ClientResources client) {
//		System.out.println("oauth2ClientContext " + oauth2ClientContext);
		return new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
	}
	
	private UserInfoTokenServices getFacebookUserInfoTokenServices(ClientResources client) {
		UserInfoTokenServices tokenServices = new UserInfoTokenServices(
				client.getResource().getUserInfoUri(), client.getClient().getClientId());
		tokenServices.setRestTemplate(getFacebookOAuth2RestTemplate(client));
		return tokenServices;
	}
	
	@Bean
	@ConfigurationProperties("facebook")
	public ClientResources facebook() {
		return new ClientResources();
	}
	
	@Bean
	@ConfigurationProperties("github")
	public ClientResources github() {
		return new ClientResources();
	}
}

class ClientResources {

	@NestedConfigurationProperty
	private AuthorizationCodeResourceDetails client = new AuthorizationCodeResourceDetails();

	@NestedConfigurationProperty
	private ResourceServerProperties resource = new ResourceServerProperties();

	public AuthorizationCodeResourceDetails getClient() {
		return client;
	}

	public ResourceServerProperties getResource() {
		return resource;
	}
}