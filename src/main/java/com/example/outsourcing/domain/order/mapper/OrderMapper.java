package com.example.outsourcing.domain.order.mapper;

import com.example.outsourcing.domain.order.dto.OrderResponseDto;
import com.example.outsourcing.domain.order.entity.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponseDto toDto(Order order) {
        return new OrderResponseDto(
            order.getUser().getUsername(),
            order.getStatus(),
            order.getOrderMenus()
        );
    }
}
