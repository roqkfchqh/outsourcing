package com.example.outsourcing.domain.shop.entity;

import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "menus")
@NoArgsConstructor
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    private String name;
    private String description;

    private BigDecimal price = BigDecimal.ZERO;

    private boolean isDeleted = false;

    // 커스텀 생성자 추가
    private Menu(Shop shop, String name, String description, BigDecimal price) {
        this.shop = shop;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public static Menu create(Shop shop, String name, String description, BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidRequestException(ErrorCode.MIN_ORDER_PRICE_MUST_BE_GREATER_THAN_ZERO);
        }
        return new Menu(shop, name, description, price);
    }

    // 소프트 딜리트 메서드
    public void markAsDeleted() {
        this.isDeleted = true;
    }

    public void update(String name, String description, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public void validateIsActive() {
        if (isDeleted()) {
            throw new InvalidRequestException(ErrorCode.MENU_NOT_FOUND);
        }
    }
}
