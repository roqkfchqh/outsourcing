package com.example.outsourcing.domain.shop.dto;

import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class MenuResponseDto {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Long shopId;

    public MenuResponseDto(Long id, String name, String description, BigDecimal price,
        Long shopId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.shopId = shopId;
    }

}
