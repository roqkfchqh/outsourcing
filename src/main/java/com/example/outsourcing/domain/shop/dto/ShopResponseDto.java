package com.example.outsourcing.domain.shop.dto;

import java.math.BigDecimal;

public class ShopResponseDto {

    private Long id;
    private String name;
    private BigDecimal minOrderPrice;
    private boolean isDeleted;

    // 생성자, Getter
    public ShopResponseDto(Long id, String name, BigDecimal minOrderPrice, boolean isDeleted) {
        this.id = id;
        this.name = name;
        this.minOrderPrice = minOrderPrice;
        this.isDeleted = isDeleted;
    }
}
