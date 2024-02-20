package com.ex.palago.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
public class JwtTokenInfo {
	private final String accessToken;

	public JwtTokenInfo(String accessToken) {
		this.accessToken = accessToken;
	}
}
