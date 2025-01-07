package com.example.outsourcing.domain.cart.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Cart {

    private Long id;
    private List<MenuItem> items = new ArrayList<>();
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private int totalQuantity;

    @Data
    @NoArgsConstructor
    public static class MenuItem {

        private Long menuId;
        private int quantity;
        private BigDecimal price;
    }
}
