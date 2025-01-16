package com.example.outsourcing.domain.order.entity;

import com.example.outsourcing.domain.cart.entity.Cart;
import com.example.outsourcing.domain.cart.entity.Cart.MenuItem;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public record Items(List<Cart.MenuItem> items) {

    @Override
    public List<Cart.MenuItem> items() {
        return Collections.unmodifiableList(items);
    }

    public BigDecimal calculateTotalPrice(Menus menus) {
        return items.stream()
            .map(item -> menus.get(item.getMenuId())
                .getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addItem(Long menuId) {
        items.stream()
            .filter(item -> item.getMenuId().equals(menuId))
            .findFirst()
            .ifPresentOrElse(
                MenuItem::increase,
                () -> items.add(new MenuItem(menuId))
            );
    }

    public void removeItem(Long menuId) {
        MenuItem menuItem = items.stream()
            .filter(item -> item.getMenuId().equals(menuId))
            .findFirst()
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.CART_ITEM_NOT_FOUND));
        menuItem.decrease();

        if (menuItem.getQuantity() == 0) {
            items.remove(menuItem); // 수량이 0이면 리스트에서 삭제
        }
    }

    public List<Long> getMenuIds() {
        return items.stream()
            .map(MenuItem::getMenuId)
            .toList();
    }
}
