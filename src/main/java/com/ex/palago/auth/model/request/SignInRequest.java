package com.ex.palago.auth.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignInRequest {
	private String username;
	private String password;
}
