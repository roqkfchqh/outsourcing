package com.example.outsourcing.domain.order.mapper;

import com.example.outsourcing.domain.order.dto.OrderMenuResponseDto;
import com.example.outsourcing.domain.order.dto.OrderResponseDto;
import com.example.outsourcing.domain.order.entity.Order;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponseDto toDto(Order order, List<OrderMenuResponseDto> orderMenuResponseDto) {
        BigDecimal totalPrice = calculateTotalPrice(orderMenuResponseDto);

        return new OrderResponseDto(
            order.getShop().getName(),
            order.getUser().getUsername(),
            order.getStatus(),
            orderMenuResponseDto,
            totalPrice
        );
    }

    private BigDecimal calculateTotalPrice(List<OrderMenuResponseDto> orderMenuResponseDto) {
        return orderMenuResponseDto.stream()
            .map(menu -> menu.price().multiply(BigDecimal.valueOf(menu.quantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
