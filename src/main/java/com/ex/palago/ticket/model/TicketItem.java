package com.ex.palago.ticket.model;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Setter
@Slf4j
@Table(name = "tickets")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TicketPayType payType;

    private String name;

    private String description;

    private int price;

    // 재고
    private Long quantity;

    // 공급량
    private Long supplyCount;

    // 구매제한
    private Long purchaseLimit;

    // 승인 타입
    @Enumerated(EnumType.STRING)
    private TicketType type;

    // 계좌정보
    @Embedded
    private AccountInfo accountInfo;

    // 재고 공개 여부
    private Boolean isQuantityPublic;

    // 판매 가능 여부
    private Boolean isSellable;

    // 판매 시작 시간
    private LocalDateTime saleStartAt;

    // 판매 종료 시간
    private LocalDateTime saleEndAt;

    // 상태
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "'VALID")
    private TicketItemStatus ticketItemStatus = TicketItemStatus.VALID;

    private Long eventId;


    public void addItemOptionGroup(OptionGroup optionGroup) {
        // 재고 감소된 티켓상품은 옵션적용 변경 불가
        if(this.isQuantityReduce()){
            // TODO
            // 예외처리 넘기기
            log.error("quantity check caused error");
        }

        // 무료 티켓에 유료 옵션 적용 불가
        if(this.payType.equals(TicketPayType.FREE_TICKET) && optionGroup.getOptions().stream()
            .anyMatch(
                option -> option.getAdditionalPrice().isGreaterThan(0);
            )
        )

    }

    public Boolean isQuantityReduce() {
        return !this.quantity.equals(this.supplyCount);
    }
}
