package com.ex.palago.ticket.model;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TicketType {
	// 선착순
	FIRST_COME_FIRST_SERVED("FIRST_COME_FIRST_SERVED", "선착순"),

	APPROVAL("APPROVAL", "승인");

	private String value;

	@JsonValue private String kr;

	/** 선착순 방식인지 반환하는 메서드 */
	public Boolean isFCFS() {
		return this.equals(TicketType.FIRST_COME_FIRST_SERVED);
	}

}

