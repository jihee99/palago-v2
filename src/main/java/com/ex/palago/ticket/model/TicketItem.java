package com.ex.palago.ticket.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.util.StringUtils;

import com.ex.palago.common.vo.Money;

@Entity
@Getter
@Setter
@Slf4j
@Table(name = "tickets")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketItem {

    public static final Long MINIMUM_PAYMENT_WON = 1000L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TicketPayType payType;

    private String name;

    private String description;

    private Money price;

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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private final List<ItemOptionGroup> itemOptionGroups = new ArrayList<>();


    public void addItemOptionGroup(OptionGroup optionGroup) {
        // 재고 감소된 티켓상품은 옵션적용 변경 불가
        if (this.isQuantityReduced()) {
            log.error("add item option group method caused error");
            // throw ForbiddenOptionChangeException.EXCEPTION;
        }

        // 무료티켓에 유료 옵션 적용 불가
        if (this.payType.equals(TicketPayType.FREE_TICKET)
            && optionGroup.getOptions().stream()
            .anyMatch(
                option -> option.getAdditionalPrice().isGreaterThan(Money.ZERO))) {
            // throw ForbiddenOptionPriceException.EXCEPTION;
        }

        // 중복 체크
        if (this.hasItemOptionGroup(optionGroup.getId())) {
            // throw DuplicatedItemOptionGroupException.EXCEPTION;
        }
        ItemOptionGroup itemOptionGroup =
            ItemOptionGroup.builder().item(this).optionGroup(optionGroup).build();
        this.itemOptionGroups.add(itemOptionGroup);
    }

    public void removeItemOptionGroup(OptionGroup optionGroup) {
        // 재고 감소된 티켓상품은 옵션적용 변경 불가
        if (this.isQuantityReduced()) {
            // throw ForbiddenOptionChangeException.EXCEPTION;
        }
        ItemOptionGroup itemOptionGroup = this.findItemOptionGroup(optionGroup);
        this.itemOptionGroups.remove(itemOptionGroup);
    }

    public ItemOptionGroup findItemOptionGroup(OptionGroup optionGroup) {
        return this.itemOptionGroups.stream()
            .filter(itemOptionGroup -> itemOptionGroup.getOptionGroup().equals(optionGroup))
            .findAny()
            .orElseThrow();
            // .orElseThrow(() ->
            //     NotAppliedItemOptionGroupException.EXCEPTION);
    }

    public Boolean hasItemOptionGroup(Long optionGroupId) {
        return this.itemOptionGroups.stream()
            .anyMatch(
                itemOptionGroup ->
                    itemOptionGroup.getOptionGroup().getId().equals(optionGroupId));
    }

    public void softDeleteTicketItem() {
        // 재고 감소된 티켓상품은 삭제 불가z
        if (this.isQuantityReduced()) {
            // throw ForbiddenTicketItemDeleteException.EXCEPTION;
        }
        this.ticketItemStatus = TicketItemStatus.DELETED;
    }

    public void validateEventId(Long eventId) {
        if (!this.getEventId().equals(eventId)) {
            // throw InvalidTicketItemException.EXCEPTION;
        }
    }

    public void validateTicketPayType(Boolean isPartner) {

        // 유료티켓은 무조건 선착순 + 제휴 확인 + 1000원 이상
        if (payType == TicketPayType.PRICE_TICKET) {
            if (type != TicketType.FIRST_COME_FIRST_SERVED) {
                // throw InvalidTicketTypeException.EXCEPTION;
            }
            if (!isPartner) {
                // throw InvalidPartnerException.EXCEPTION;
            }
            if (price.isLessThan(Money.wons(MINIMUM_PAYMENT_WON))) {
                // throw InvalidTicketPriceException.EXCEPTION;
            }
        }
        // 무료티켓은 무조건 0원
        else {
            if (!price.equals(Money.ZERO)) {
                // throw InvalidTicketPriceException.EXCEPTION;
            }
        }
    }

    /** 선착순 결제인지 확인하는 메서드 */
    public Boolean isFCFS() {
        return this.type.isFCFS();
    }

    public Boolean hasOption() {
        return !itemOptionGroups.isEmpty();
    }

    public List<Long> getOptionGroupIds() {
        return itemOptionGroups.stream()
            .map(itemOptionGroup -> itemOptionGroup.getOptionGroup().getId())
            .sorted()
            .toList();
    }

    public Boolean isQuantityReduced() {
        return !this.quantity.equals(this.supplyCount);
    }

    public void reduceQuantity(Long quantity) {
        if (this.quantity < 0) {
            // throw TicketItemQuantityException.EXCEPTION;
        }
        validEnoughQuantity(quantity);
        this.quantity = this.quantity - quantity;
    }

    public void validEnoughQuantity(Long quantity) {
        if (this.quantity < quantity) {
            // throw TicketItemQuantityLackException.EXCEPTION;
        }
    }

    public void validPurchaseLimit(Long quantity) {
        if (isPurchaseLimitExceed(quantity)) {
            // throw TicketPurchaseLimitException.EXCEPTION;
        }
    }

    public Boolean isPurchaseLimitExceed(Long quantity) {
        return this.purchaseLimit < quantity;
    }

    public void increaseQuantity(Long quantity) {
        if (this.quantity + quantity > supplyCount) {
            // throw TicketItemQuantityLargeException.EXCEPTION;
        }
        this.quantity = this.quantity + quantity;
    }

    public Boolean isSold() {
        return quantity < supplyCount;
    }

    public Boolean isQuantityLeft() {
        return quantity > 0;
    }

    public Long getEventId() {
        return eventId;
    }

}

