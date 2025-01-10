package com.example.outsourcing.domain.shop.dto;

import java.math.BigDecimal;
import java.time.LocalTime;

public class ShopResponseDto {

    private Long id;
    private String name;
    private BigDecimal minOrderPrice;
    private LocalTime open;    // 추가
    private LocalTime close;   // 추가
    private boolean isDeleted;

    // 생성자, Getter
    public ShopResponseDto(Long id, String name, BigDecimal minOrderPrice, LocalTime open,
        LocalTime close, boolean isDeleted) {
        this.id = id;
        this.name = name;
        this.minOrderPrice = minOrderPrice;
        this.open = open;
        this.close = close;
        this.isDeleted = isDeleted;
    }
}
