package com.example.outsourcing.domain.order.mapper;

import com.example.outsourcing.domain.cart.entity.OrderMenu;
import com.example.outsourcing.domain.order.dto.OrderMenuResponseDto;

public class OrderMenuMapper {

    public static OrderMenuResponseDto toDto(OrderMenu orderMenu) {
        return new OrderMenuResponseDto(
            orderMenu.getMenu().getName(),
            orderMenu.getQuantity(),
            orderMenu.getMenu().getPrice()
        );
    }

}
