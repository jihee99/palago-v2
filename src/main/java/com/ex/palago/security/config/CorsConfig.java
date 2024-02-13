package com.ex.palago.security.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowCredentials(true);
		configuration.addAllowedOrigin("*");
		configuration.addAllowedHeader("*");
		configuration.setAllowedMethods(getHttpMethods());

		source.registerCorsConfiguration("/api/**", config);
		return new CorsFilter(source);
	}

	private List<String> getHttpMethods() {
		return List.of(
			HttpMethod.GET.name(), HttpMethod.POST.name(),
			HttpMethod.PUT.name(), HttpMethod.OPTIONS.name()
		);
	}

}
