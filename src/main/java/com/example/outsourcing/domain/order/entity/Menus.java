package com.example.outsourcing.domain.order.entity;

import com.example.outsourcing.domain.cart.entity.Cart;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;
import com.example.outsourcing.domain.shop.entity.Menu;
import java.util.Collections;
import java.util.Map;

public record Menus(Map<Long, Menu> menus) {

    @Override
    public Map<Long, Menu> menus() {
        return Collections.unmodifiableMap(menus);
    }

    public Menu get(Long menuId) {
        if (!menus.containsKey(menuId)) {
            throw new InvalidRequestException(ErrorCode.MENU_NOT_FOUND);
        }
        return menus.get(menuId);
    }

    public void validateItem(Cart.MenuItem item) {
        Menu menu = menus.get(item.getMenuId());
        if (menu == null) {
            throw new InvalidRequestException(ErrorCode.MENU_NOT_FOUND);
        }
        menu.validateIsActive();
    }
}
