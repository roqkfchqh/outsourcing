package com.example.outsourcing.domain.order.dto;

import com.example.outsourcing.domain.cart.entity.OrderMenu;
import com.example.outsourcing.domain.order.entity.Order.Status;
import java.util.List;

public record OrderResponseDto(String username, Status status, List<OrderMenu> orderMenu) {

}
