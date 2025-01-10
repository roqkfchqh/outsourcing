package com.example.outsourcing.domain.shop.Mapper;

import com.example.outsourcing.domain.shop.dto.ShopResponseDto;
import com.example.outsourcing.domain.shop.entity.Shop;
import org.springframework.stereotype.Component;

@Component
public class ShopMapper {

    public ShopResponseDto toResponseDto(Shop shop) {
        return new ShopResponseDto(
            shop.getId(),
            shop.getName(),
            shop.getMinOrderPrice(),
            shop.getOpen(),
            shop.getClose(),
            shop.isDeleted()
        );
    }
}
