package com.ex.palago.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class JwtTokenInfo {
	private String grantType;
	private String accessToken;
	private String refreshToken;
}