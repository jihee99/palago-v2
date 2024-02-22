package com.ex.palago.security.config;

import com.ex.palago.security.jwt.JwtTokenService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.ex.palago.member.repository.MemberRepository;
import com.ex.palago.security.filter.JwtAuthenticationFilter;
import com.ex.palago.security.filter.JwtAuthorizationFilter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final AuthenticationConfiguration authenticationConfiguration;
	private final CorsConfig corsConfig;
	private final MemberRepository memberRepository;
	private final JwtTokenService tokenService;

	@Bean
	public AuthenticationManager authenticationManager(
			AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * 이 메서드는 정적 자원에 대해 보안을 적용하지 않도록 설정한다.
	 * 정적 자원은 보통 HTML, CSS, JavaScript, 이미지 파일 등을 의미하며, 이들에 대해 보안을 적용하지 않음으로써 성능을 향상시킬 수 있다.
	 */
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring()
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}


	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf().disable();

		http.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)

				.and()
				.formLogin().disable()
				.httpBasic().disable()

				 .apply(new MyCustomDsl())

				.and()
				.authorizeRequests(authroize -> authroize
						.antMatchers("/", "/home", "/login", "/logout", "/sign", "/join").permitAll()

//						.antMatchers("/api/v1/user/**").hasAnyRole("ROLE_USER", "ROLE_SELLER", "ROLE_ADMIN")
						 .antMatchers("/api/v1/user/**")
						 .access("hasRole('ROLE_USER') or hasRole('ROLE_SELLER') or hasRole('ROLE_ADMIN')")

						.antMatchers("/api/v1/seller/**")
						.access("hasRole('ROLE_SELLER') or hasRole('ROLE_ADMIN')")

						.antMatchers("/api/v1/admin/**")
						.access("hasRole('ROLE_ADMIN')")

						.anyRequest().authenticated()
				);

//		http.addFilterBefore(new JwtAuthenticationFilter(
//				authenticationManager(authenticationConfiguration), tokenService), UsernamePasswordAuthenticationFilter.class);
//
//		http.addFilterBefore(new JwtAuthorizationFilter(
//				authenticationManager(authenticationConfiguration), tokenService, memberRepository), BasicAuthenticationFilter.class);

		return http.build();

	}

	public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
		@Override
		public void configure(HttpSecurity http) throws Exception {
//			AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
			http
				.addFilter(corsConfig.corsFilter())
				.addFilterBefore(new JwtAuthenticationFilter(authenticationManager(authenticationConfiguration), tokenService), UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(new JwtAuthorizationFilter(authenticationManager(authenticationConfiguration), tokenService, memberRepository), BasicAuthenticationFilter.class);
//				.addFilter(new JwtAuthenticationFilter(authenticationManager(authenticationConfiguration), tokenService))
//				.addFilter(new JwtAuthorizationFilter(authenticationManager(authenticationConfiguration), tokenService, memberRepository));
		}
	}

}
