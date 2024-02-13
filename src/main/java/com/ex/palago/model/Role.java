package com.ex.palago.model;

public enum Role {

	ADMIN("ROLE_ADMIN"),
	SELLER("ROLE_SELLER"),
	USER("ROLE_USER");

	private final String key;

	Role(String role) {
		this.key = role;
	}

	public String getKey() {
		return key;
	}

}
