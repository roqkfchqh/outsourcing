package com.example.outsourcing.domain.order.mapper;

import com.example.outsourcing.domain.order.dto.OrderMenuResponseDto;
import com.example.outsourcing.domain.order.dto.OrderResponseDto;
import com.example.outsourcing.domain.order.entity.Order;
import java.math.BigDecimal;
import java.util.List;

public class OrderMapper {

    public static OrderResponseDto toDto(
        String shopName,
        Order order,
        List<OrderMenuResponseDto> orderMenuResponseDto
    ) {
        BigDecimal totalPrice = calculateTotalPrice(orderMenuResponseDto);

        return new OrderResponseDto(
            shopName,
            order.getUser().getUsername(),
            order.getStatus(),
            orderMenuResponseDto,
            totalPrice
        );
    }

    private static BigDecimal calculateTotalPrice(List<OrderMenuResponseDto> orderMenuResponseDto) {
        return orderMenuResponseDto.stream()
            .map(menu -> menu.price().multiply(BigDecimal.valueOf(menu.quantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
