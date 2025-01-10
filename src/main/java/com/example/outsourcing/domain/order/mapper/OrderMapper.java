package com.example.outsourcing.domain.order.mapper;

import com.example.outsourcing.domain.order.dto.OrderMenuResponseDto;
import com.example.outsourcing.domain.order.dto.OrderResponseDto;
import com.example.outsourcing.domain.order.entity.Order;
import java.util.List;

public class OrderMapper {

    public static OrderResponseDto toDto(
        Long shopId,
        Long ownerId,
        String shopName,
        Order order,
        List<OrderMenuResponseDto> orderMenuResponseDto
    ) {

        return new OrderResponseDto(
            shopId,
            order.getId(),
            ownerId,
            shopName,
            order.getUser().getId(),
            order.getStatus(),
            orderMenuResponseDto,
            order.getTotalPrice(),
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
    }

    public static OrderResponseDto toDto(
        Long shopId,
        String shopName,
        Order order,
        List<OrderMenuResponseDto> orderMenuResponseDto
    ) {

        return new OrderResponseDto(
            shopId,
            order.getId(),
            null,
            shopName,
            order.getUser().getId(),
            order.getStatus(),
            orderMenuResponseDto,
            order.getTotalPrice(),
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
    }
}
