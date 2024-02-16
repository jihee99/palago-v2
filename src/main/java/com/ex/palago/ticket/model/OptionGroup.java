package com.ex.palago.ticket.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "option_group")
@Entity
public class OptionGroup {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long eventId;

	@Enumerated(EnumType.STRING)
	private OptionGroupType type;

	// 옵션 그룹 이름
	private String name;

	// 옵션 그룹 설명
	private String description;

	// 필수 응답 여부
	private Boolean isEssential;

	// 상태
	@Enumerated(EnumType.STRING)
	@ColumnDefault(value = "'VALID'")
	private OptionGroupStatus optionGroupStatus = OptionGroupStatus.VALID;


	@OneToMany(cascade = CascadeType.ALL, mappedBy = "optionGroup")
	private final List<Option> options = new ArrayList<>();

	@Builder
	public OptionGroup(
		Long eventId,
		OptionGroupType type,
		String name,
		String description,
		Boolean isEssential,
		List<Option> options
	) {
		this.eventId = eventId;
		this.type = type;
		this.name = name;
		this.description = description;
		this.isEssential = isEssential;

		this.options.addAll(options);
		options.forEach(option -> option.setOptionGroup(this));
	}

	public void validateEventId(Long eventId){
		if(!this.getEventId().equals(eventId)){
			// TODO
			// 예외처리
			System.out.println("option group class caused error");
		}
	}

	public Boolean hasApplication(List<TicketItem> ticketItems) {
		return ticketItems.stream()
			.map(ticketItem -> ticketItem.hasItemOptionGroup(this.id))
			.reduce(Boolean.FALSE, Boolean::logicalOr);
	}
}



