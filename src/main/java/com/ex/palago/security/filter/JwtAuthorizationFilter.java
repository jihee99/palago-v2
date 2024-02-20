package com.ex.palago.security.filter;

import com.ex.palago.member.model.Member;
import com.ex.palago.security.auth.PrincipalDetails;
import com.ex.palago.security.jwt.JwtTokenService;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.ex.palago.member.repository.MemberRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

	private static final String HEADER_STRING = HttpHeaders.AUTHORIZATION;
	private static final String ACCESS_TOKEN_PREFIX = "Token ";

	private final JwtTokenService tokenService;
	private final MemberRepository memberRepository;

	public JwtAuthorizationFilter(
			AuthenticationManager authenticationManager, JwtTokenService tokenService, MemberRepository memberRepository) {
		super(authenticationManager);
		this.tokenService = tokenService;
		this.memberRepository = memberRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		if (isNullToken(request)) {
			chain.doFilter(request, response);
			return;
		}

		String jwtToken = request.getHeader(HEADER_STRING).replace(ACCESS_TOKEN_PREFIX, "");

		if (tokenService.validateTokenExpirationTimeNotExpired(jwtToken)) {

			String username = tokenService.getUsername(jwtToken);
			Member member = memberRepository.findByUsername(username).orElseThrow();
			PrincipalDetails principalDetails = new PrincipalDetails(member);

			Authentication authentication =
					new UsernamePasswordAuthenticationToken(principalDetails, "", principalDetails.getAuthorities());

			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		chain.doFilter(request, response);
	}

	private boolean isNullToken(HttpServletRequest request) {
		String jwtHeader = request.getHeader(HEADER_STRING);
		if (jwtHeader == null || !jwtHeader.startsWith(ACCESS_TOKEN_PREFIX)) {
			return true;
		}
		return false;
	}
}