package com.example.outsourcing.domain.order.dto;

import com.example.outsourcing.domain.order.entity.Order.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDto(
    String shopName,
    Long userId,
    Status status,
    List<OrderMenuResponseDto> orderMenu,
    BigDecimal totalPrice,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createdAt,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime updatedAt) {

}
