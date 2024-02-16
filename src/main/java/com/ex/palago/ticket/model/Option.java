package com.ex.palago.ticket.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.security.core.parameters.P;

import com.ex.palago.common.vo.Money;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "options")
@Entity
public class Option {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String answer;

	private Money additionalPrice;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "option_group_id", updatable = false)
	private OptionGroup optionGroup;


	@Builder
	public Option (String answer, Money additionalPrice, OptionGroup optionGroup){
		this.answer = answer;
		this.additionalPrice = additionalPrice;
		this.optionGroup = optionGroup;
	}


	public static Option create(String answer, Money additionalPrice, OptionGroup optionGroup) {
		return Option.builder()
			.answer(answer)
			.additionalPrice(additionalPrice)
			.optionGroup(optionGroup)
			.build();
	}

	public void setOptionGroup(OptionGroup optionGroup) {
		this.optionGroup = optionGroup;
	}

	public Long getOptionGroupId() {
		return this.optionGroup.getId();
	}

	public String getQuestionDescription() {
		return this.optionGroup.getDescription();
	}

	public String getQuestionName() {
		return this.optionGroup.getName();
	}

	public OptionGroupType optionGroupType(){
		return this.optionGroup.getType();
	}

	public void validCorrectAnswer(String answer){
		OptionGroupType type = optionGroup.getType();
		if(type == OptionGroupType.TRUE_FALSE) {
			if(!isAnswerTrueFalse(answer)){
				// TODO
				// Exception 처리하기
				System.out.println("answer error");
			}
		}
	}

	private Boolean isAnswerTrueFalse(String answer) {
		return answer.equals("예") || answer.equals("아니요");
	}

}
