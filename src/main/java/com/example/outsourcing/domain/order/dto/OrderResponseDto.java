package com.example.outsourcing.domain.order.dto;

import com.example.outsourcing.domain.order.entity.Order.Status;
import java.math.BigDecimal;
import java.util.List;

public record OrderResponseDto(
    String shopName,
    String username,
    Status status,
    List<OrderMenuResponseDto> orderMenu,
    BigDecimal totalPrice) {

}
