package com.ex.palago.security.filter;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import com.auth0.jwt.JWT;
//import com.auth0.jwt.algorithms.Algorithm;
import com.ex.palago.auth.model.request.SignInRequest;
import com.ex.palago.security.auth.PrincipalDetails;
import com.ex.palago.security.jwt.JwtProperties;
import com.ex.palago.security.jwt.JwtTokenService;
import com.ex.palago.security.jwt.Token;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ex.palago.member.dto.LoginRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private static final String HEADER_STRING = HttpHeaders.AUTHORIZATION;
	private static final String ACCESS_TOKEN_PREFIX = "Token ";

	private final AuthenticationManager authenticationManager;

	private final JwtTokenService tokenService;

	// Authentication 객체 만들어서 리턴 => 의존 : AuthenticationManager
	// 인증 요청시에 실행되는 함수 => /login
	@Override
	public Authentication attemptAuthentication(
			HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		try {
			SignInRequest signInRequest = getSignInRequest(request.getInputStream());

			UsernamePasswordAuthenticationToken authenticationToken
					= createUsernamePasswordAuthenticationToken(signInRequest);

			return authenticationManager.authenticate(authenticationToken);
		} catch (IOException e) {
			e.printStackTrace();
			return super.attemptAuthentication(request, response);
		}
	}

	private UsernamePasswordAuthenticationToken createUsernamePasswordAuthenticationToken(SignInRequest signInRequest) {
		return new UsernamePasswordAuthenticationToken(
				signInRequest.getUsername(), signInRequest.getPassword());
	}

	private SignInRequest getSignInRequest(ServletInputStream inputStream) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(inputStream, SignInRequest.class);
	}

	@Override
	public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
		String grantedAuthority = authResult.getAuthorities().stream().findAny().orElseThrow().toString();
		Token jwtToken = tokenService.createToken(authResult.getPrincipal().toString(), grantedAuthority);

		response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.AUTHORIZATION);
		response.addHeader(HEADER_STRING, ACCESS_TOKEN_PREFIX + jwtToken.getAccessToken());
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
			throws IOException, ServletException {
		super.unsuccessfulAuthentication(request, response, failed);
	}

	@Getter
	@Setter
	@NoArgsConstructor
	private static class SignInRequest {

		private String username;
		private String password;
	}

}
