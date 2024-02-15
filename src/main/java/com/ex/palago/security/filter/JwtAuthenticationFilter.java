package com.ex.palago.security.filter;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import com.auth0.jwt.JWT;
//import com.auth0.jwt.algorithms.Algorithm;
import com.ex.palago.security.auth.PrincipalDetails;
import com.ex.palago.security.jwt.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
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

	private final AuthenticationManager authenticationManager;

	private final JwtProperties jwtProperties;


	// Authentication 객체 만들어서 리턴 => 의존 : AuthenticationManager
	// 인증 요청시에 실행되는 함수 => /login
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		log.info("{}", "JwtAuthenticationFilter : 진입");

		// request에 있는 username과 password를 파싱해서 자바 Object로 받기
		ObjectMapper mapper = new ObjectMapper();
		LoginRequestDto loginRequestDto = null;
		try {
			loginRequestDto = mapper.readValue(request.getInputStream(), LoginRequestDto.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("JwtAuthenticationFilter : " + loginRequestDto);

		// 유저네임패스워드 토큰 생성
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword());
		System.out.println("JwtAuthenticationFilter : success create token");

		// authenticate() 함수가 호출 되면 인증 프로바이더가 유저 디테일 서비스의
		// loadUserByUsername(토큰의 첫번째 파라메터) 를 호출하고
		// UserDetails를 리턴받아서 토큰의 두번째 파라메터(credential)과
		// UserDetails(DB값)의 getPassword()함수로 비교해서 동일하면
		// Authentication 객체를 만들어서 필터체인으로 리턴해준다.

		// Tip: 인증 프로바이더의 디폴트 서비스는 UserDetailsService 타입
		// Tip: 인증 프로바이더의 디폴트 암호화 방식은 BCryptPasswordEncoder
		// 결론은 인증 프로바이더에게 알려줄 필요가 없음.

		Authentication authentication = authenticationManager.authenticate(authenticationToken);

		PrincipalDetails principalDetailis = (PrincipalDetails) authentication.getPrincipal();
		System.out.println("Authentication : " + principalDetailis.getMember().getUsername());
		return authentication;
	}

	// JWT Token 생성해서 response에 담아주기
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

		PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

		System.out.println("successfulAuthentication !!");

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtProperties.getExpirationTime());

//		final Key =

		String jwtToken = Jwts.builder()
				.setSubject(principalDetails.getUsername())
				.claim("id", principalDetails.getMember().getId())
				.claim("username", principalDetails.getMember().getUsername())
				.setIssuedAt(now)
				.setExpiration(expiryDate)
//				.signWith(Keys.hmacShaKeyFor(JwtProperties.SECRET.getBytes()), SignatureAlgorithm.HS512)
				.compact();

//		String jwtToken = JWT.create()
//				.withSubject(principalDetailis.getUsername())
//				.withExpiresAt(new Date(System.currentTimeMillis()+ JwtProperties.EXPIRATION_TIME))
////				.withExpiresAt(new Date(System.currentTimeMillis()+ JwtProperties.getExpirationTime()))
//				.withClaim("id", principalDetailis.getMember().getId())
//				.withClaim("username", principalDetailis.getMember().getUsername())
//				.sign(Algorithm.HMAC512(JwtProperties.SECRET));
//				.sign(Algorithm.HMAC512(JwtProperties.getSecretKey()));

		System.out.println(jwtToken);
//		response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+jwtToken);
		response.addHeader(jwtProperties.getHeaderString(), jwtProperties.getPrefix()+jwtToken);
	}

	private Key getSecretKey() {
		return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
	}


}
