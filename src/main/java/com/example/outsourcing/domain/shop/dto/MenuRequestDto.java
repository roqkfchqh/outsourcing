package com.example.outsourcing.domain.shop.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class MenuRequestDto {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "가격은 0보다 커야 합니다.")
    private BigDecimal price;

    public MenuRequestDto(String name, String description, BigDecimal price, Long shopId) {
        this.name = name;
        this.description = description;
        this.price = price;
    }
}
