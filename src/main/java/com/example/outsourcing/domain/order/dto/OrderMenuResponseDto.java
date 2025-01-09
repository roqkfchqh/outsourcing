package com.example.outsourcing.domain.order.dto;

import java.math.BigDecimal;

public record OrderMenuResponseDto(String name, int quantity, BigDecimal price) {

}
