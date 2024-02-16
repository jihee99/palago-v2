package com.ex.palago.ticket.model;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "item_option_group", uniqueConstraints = {@UniqueConstraint(columnNames = {"item_id", "option_group_id"})})
public class ItemOptionGroup {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "item_option_group_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id", updatable = false)
	private TicketItem item;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "option_group_id", updatable = false)
	private OptionGroup optionGroup;

	@Builder
	public ItemOptionGroup(TicketItem item, OptionGroup optionGroup) {
		this.item = item;
		this.optionGroup = optionGroup;
	}
}
