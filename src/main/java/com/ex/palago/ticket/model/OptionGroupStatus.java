package com.ex.palago.ticket.model;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OptionGroupStatus {
	VALID("VALID", "유효"),

	DELETED("DELETED", "삭제됨");

	private final String value;

	@JsonValue private final String kr;
}
