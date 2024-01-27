package com.ecolink.core.common.config.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ecolink.core.auth.handler.OAuth2SuccessHandler;
import com.ecolink.core.auth.service.OAuth2UserLoadingService;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private final OAuth2SuccessHandler oauth2SuccessHandler;
	private final OAuth2UserLoadingService oauth2UserLoadingService;
	private final String apiPrefix;

	public SecurityConfig(OAuth2SuccessHandler oauth2SuccessHandler, OAuth2UserLoadingService oauth2UserLoadingService,
		@Value("${api.prefix}") String apiPrefix) {
		this.oauth2SuccessHandler = oauth2SuccessHandler;
		this.oauth2UserLoadingService = oauth2UserLoadingService;
		this.apiPrefix = apiPrefix;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.cors(cors -> cors.configurationSource(websiteConfigurationSource()))
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.oauth2Login(oauth ->
				oauth.authorizationEndpoint(authorization -> authorization.baseUri(apiPrefix + "/oauth2/authorization"))
					.redirectionEndpoint(redirection -> redirection.baseUri(apiPrefix + "/oauth2/code"))
					.userInfoEndpoint(userInfo -> userInfo.userService(oauth2UserLoadingService))
					.successHandler(oauth2SuccessHandler))
			.headers(headers ->
				headers.addHeaderWriter(
					new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
			.authorizeHttpRequests(r -> r.anyRequest().permitAll());

		return http.build();
	}

	CorsConfigurationSource websiteConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:3000", "https://eco-link.netlify.app"));
		configuration.setAllowedMethods(List.of("GET", "POST", "DELETE", "PUT", "PATCH"));
		configuration.setAllowedHeaders(List.of(AUTHORIZATION_HEADER));
		configuration.setExposedHeaders(List.of(AUTHORIZATION_HEADER));
		configuration.setMaxAge(3600L);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	static HttpSessionIdResolver httpSessionIdResolver() {
		return new HeaderHttpSessionIdResolver(AUTHORIZATION_HEADER);
	}

}
