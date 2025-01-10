package com.example.outsourcing.domain.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalTime;
import lombok.Getter;

@Getter
public class ShopRequestDto {

    @NotBlank
    private String name;

    @NotNull
    private BigDecimal minOrderPrice;

    @NotNull
    private LocalTime open;

    @NotNull
    private LocalTime close;

    public ShopRequestDto(String name, BigDecimal minOrderPrice, LocalTime open, LocalTime close) {
        this.name = name;
        this.minOrderPrice = minOrderPrice;
        this.open = open;
        this.close = close;
    }
}
