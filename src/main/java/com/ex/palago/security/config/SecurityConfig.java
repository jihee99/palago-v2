package com.ex.palago.security.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.ex.palago.member.repository.MemberRepository;
import com.ex.palago.security.jwt.JwtAuthenticationFilter;
import com.ex.palago.security.jwt.JwtAuthorizationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


	private final MemberRepository memberRepository;

	private final CorsConfig corsConfig;


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
						.antMatchers("/login", "/logout", "/sign", "/join").permitAll()

						.antMatchers("/api/v1/user/**")
						.access("hasRole('ROLE_USER') or hasRole('ROLE_SELLER') or hasRole('ROLE_ADMIN')")

						.antMatchers("/api/v1/seller/**")
						.access("hasRole('ROLE_SELLER') or hasRole('ROLE_ADMIN')")
						.antMatchers("/api/v1/admin/**")

						.access("hasRole('ROLE_ADMIN')")
						.anyRequest().permitAll());


		return http.build();

	}

	public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
		@Override
		public void configure(HttpSecurity http) throws Exception {
			AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
			http
				.addFilter(corsConfig.corsFilter())
				.addFilter(new JwtAuthenticationFilter(authenticationManager))
				.addFilter(new JwtAuthorizationFilter(authenticationManager, memberRepository));
		}
	}

}
