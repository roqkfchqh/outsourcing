package com.example.outsourcing.domain.order.service;

import com.example.outsourcing.domain.cart.entity.Cart;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;
import com.example.outsourcing.domain.shop.entity.Menu;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.MenuRepository;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCartValidator {

    private final MenuRepository menuRepository;
    private final ShopRepository shopRepository;

    public Map<Long, Menu> validateCartAndReturnMenu(Cart cart) {
        Map<Long, Menu> menus = findMenusByCart(cart);

        for (Cart.MenuItem item : cart.getItems()) {
            Menu menu = menus.get(item.getMenuId());
            if (menu == null) {
                throw new InvalidRequestException(ErrorCode.MENU_NOT_FOUND);
            }
            menu.validateIsActive();
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

    private Map<Long, Menu> findMenusByCart(Cart cart) {
        List<Long> menuIds = cart.getMenuIds();
        List<Menu> menus = menuRepository.findByIdIn(menuIds);
        return menus.stream().collect(Collectors.toMap(Menu::getId, Function.identity()));
    }

}
