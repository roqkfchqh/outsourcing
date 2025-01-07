package com.example.outsourcing.domain.order.dto;

import com.example.outsourcing.domain.order.entity.Order.Status;
import java.util.List;

public record OrderResponseDto(
    String shopname,
    String username,
    Status status,
    List<OrderMenuResponseDto> orderMenu) {

}
