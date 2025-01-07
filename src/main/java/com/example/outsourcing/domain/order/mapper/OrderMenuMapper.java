package com.example.outsourcing.domain.order.mapper;

import com.example.outsourcing.domain.cart.entity.OrderMenu;
import com.example.outsourcing.domain.order.dto.OrderMenuResponseDto;
import org.springframework.stereotype.Component;

@Component
public class OrderMenuMapper {

    public OrderMenuResponseDto toDto(OrderMenu orderMenu) {
        return new OrderMenuResponseDto(
            orderMenu.getMenu().getName(),
            orderMenu.getQuantity(),
            orderMenu.getMenu().getPrice()
        );
    }

}
