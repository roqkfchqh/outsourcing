package com.example.outsourcing.domain.order.mapper;

import com.example.outsourcing.domain.order.dto.OrderMenuResponseDto;
import com.example.outsourcing.domain.order.entity.OrderMenu;

public class OrderMenuMapper {

    public static OrderMenuResponseDto toDto(OrderMenu orderMenu) {
        return new OrderMenuResponseDto(
            orderMenu.getMenu().getName(),
            orderMenu.getQuantity(),
            orderMenu.getMenu().getPrice()
        );
    }

}
