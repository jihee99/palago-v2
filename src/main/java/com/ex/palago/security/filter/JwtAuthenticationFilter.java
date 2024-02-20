package com.ex.palago.security.filter;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.ex.palago.security.auth.PrincipalDetails;
import com.ex.palago.security.jwt.JwtTokenService;
import com.ex.palago.security.jwt.Token;
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
			log.info("{} JwtAuthenticationFilter : " , signInRequest);

			UsernamePasswordAuthenticationToken authenticationToken = createUsernamePasswordAuthenticationToken(signInRequest);
			log.info("JwtAuthenticationFilter : success create token");

			// authenticate() 함수가 호출 되면 인증 프로바이더가 유저 디테일 서비스의
			// loadUserByUsername(토큰의 첫번째 파라메터) 를 호출하고
			// UserDetails를 리턴받아서 토큰의 두번째 파라메터(credential)과
			// UserDetails(DB값)의 getPassword()함수로 비교해서 동일하면
			// Authentication 객체를 만들어서 필터체인으로 리턴해준다.



			// Tip: 인증 프로바이더의 디폴트 서비스는 UserDetailsService 타입
			// Tip: 인증 프로바이더의 디폴트 암호화 방식은 BCryptPasswordEncoder
			// 결론은 인증 프로바이더에게 알려줄 필요가 없음.
			return authenticationManager.authenticate(authenticationToken);

		} catch (IOException e) {
			e.printStackTrace();
			log.error("JwtAuthenticationFilter login request dto parsing : ", e);
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

		log.info(grantedAuthority);
		log.info(authResult.getPrincipal().toString());
		log.info("successfulAuthentication !!");

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
