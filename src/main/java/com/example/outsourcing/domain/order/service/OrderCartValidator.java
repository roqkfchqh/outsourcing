package com.example.outsourcing.domain.order.service;

import com.example.outsourcing.domain.cart.entity.Cart;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;
import com.example.outsourcing.domain.shop.entity.Menu;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.MenuRepository;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import java.math.BigDecimal;
import java.time.LocalTime;
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
        // 메뉴 조회
        List<Long> menuIds = cart.getItems().stream()
            .map(Cart.MenuItem::getMenuId)
            .toList();
        Map<Long, Menu> menus = findMenusByIds(menuIds);

        for (Cart.MenuItem item : cart.getItems()) {
            Menu menu = menus.get(item.getMenuId());
            //해당 메뉴가 유효한지 검증
            if (menu == null || menu.isDeleted()) {
                throw new InvalidRequestException(ErrorCode.MENU_NOT_FOUND);
            }
        }
        return menus;
    }

    public Shop validateShop(Long shopId, BigDecimal totalPrice) {
        Shop shop = shopRepository.findById(shopId)
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.SHOP_NOT_FOUND));
        LocalTime now = LocalTime.now();
        if (now.isBefore(shop.getOpen()) || now.isAfter(shop.getClose())) {
            throw new InvalidRequestException(ErrorCode.SHOP_CLOSED);
        }
        if (shop.isDeleted()) {
            throw new InvalidRequestException(ErrorCode.SHOP_DELETED);
        }
        if (totalPrice.compareTo(shop.getMinOrderPrice()) < 0) {
            throw new InvalidRequestException(ErrorCode.MINIMUM_ORDER_NOT_MET);
        }
        return shop;
    }

    private Map<Long, Menu> findMenusByIds(List<Long> menuIds) {
        List<Menu> menus = menuRepository.findByIdIn(menuIds);
        return menus.stream().collect(Collectors.toMap(Menu::getId, Function.identity()));
    }

}
