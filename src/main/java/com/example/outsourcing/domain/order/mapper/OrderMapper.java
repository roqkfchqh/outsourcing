package com.example.outsourcing.domain.order.mapper;

import com.example.outsourcing.domain.order.dto.OrderMenuResponseDto;
import com.example.outsourcing.domain.order.dto.OrderResponseDto;
import com.example.outsourcing.domain.order.entity.Order;
import java.util.List;

public class OrderMapper {

    public static OrderResponseDto toDto(
        String shopName,
        Order order,
        List<OrderMenuResponseDto> orderMenuResponseDto
    ) {

        return new OrderResponseDto(
            shopName,
            order.getUser().getUsername(),
            order.getStatus(),
            orderMenuResponseDto,
            order.getTotalPrice()
        );
    }
}
