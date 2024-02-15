package com.ex.palago.auth.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignInRequestValidationResult {
	private final String roleKey;
}
