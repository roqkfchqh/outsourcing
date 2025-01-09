package com.example.outsourcing.domain.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class ShopRequestDto {

    @NotBlank
    private String name;

    @NotNull
    private BigDecimal minOrderPrice;

    public ShopRequestDto(String name, BigDecimal minOrderPrice) {
        this.name = name;
        this.minOrderPrice = minOrderPrice;
    }
}
