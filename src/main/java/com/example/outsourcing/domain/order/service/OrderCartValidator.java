package com.example.outsourcing.domain.order.service;

import com.example.outsourcing.domain.cart.entity.Cart;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;
import com.example.outsourcing.domain.order.entity.Menus;
import com.example.outsourcing.domain.shop.entity.Menu;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.MenuRepository;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCartValidator {

    private final MenuRepository menuRepository;
    private final ShopRepository shopRepository;

    public Menus validateCartAndReturnMenu(Cart cart) {
        Menus menus = findMenusByCart(cart);

        for (Cart.MenuItem item : cart.getItems().items()) {
            menus.validateItem(item);
        }
        return menus;
    }

    public Shop validateShop(Long shopId, BigDecimal totalPrice) {
        Shop shop = findShop(shopId);
        shop.validateIsOpened();
        shop.validateIsActive();
        shop.validateMinOrderPrice(totalPrice);
        return shop;
    }

    private Shop findShop(Long shopId) {
        return shopRepository.findById(shopId)
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.SHOP_NOT_FOUND));
    }

    private Menus findMenusByCart(Cart cart) {
        List<Long> menuIds = cart.getMenuIds();
        List<Menu> menus = menuRepository.findByIdIn(menuIds);
        return new Menus(
            menus.stream().collect(Collectors.toMap(Menu::getId, Function.identity())));
    }

}
