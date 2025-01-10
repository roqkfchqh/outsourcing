package com.example.outsourcing.domain.shop.dto;

import java.math.BigDecimal;
import java.time.LocalTime;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
public class ShopUpdateRequestDto {

    private String name;

    private BigDecimal minOrderPrice;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime open;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime close;

    public ShopUpdateRequestDto(String name, BigDecimal minOrderPrice, LocalTime open,
        LocalTime close) {
        this.name = name;
        this.minOrderPrice = minOrderPrice;
        this.open = open;
        this.close = close;
    }

}
