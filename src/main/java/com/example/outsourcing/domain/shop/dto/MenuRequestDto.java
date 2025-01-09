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
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal price;

    @NotNull
    private Long shopId;

    public MenuRequestDto(String name, String description, BigDecimal price, Long shopId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.shopId = shopId;
    }

}
