package com.example.outsourcing.domain.order.service;

import com.example.outsourcing.domain.cart.entity.Cart;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;
import com.example.outsourcing.domain.shop.entity.Menu;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.MenuRepository;
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
public class OrderCartValidation {

    private final MenuRepository menuRepository;

    public Map<Long, Menu> validateCartAndReturnMenu(Cart cart) {
        if (cart.getItems().isEmpty()) {
            throw new InvalidRequestException(ErrorCode.CART_IS_EMPTY);
        }

        // 메뉴 조회
        List<Long> menuIds = cart.getItems().stream()
            .map(Cart.MenuItem::getMenuId)
            .toList();
        Map<Long, Menu> menus = findMenusByIds(menuIds);

        // 첫 번째 메뉴의 Shop 가져오기
        Cart.MenuItem firstItem = cart.getItems().get(0);
        Menu firstMenu = menus.get(firstItem.getMenuId());
        if (firstMenu == null) {
            throw new InvalidRequestException(ErrorCode.MENU_NOT_FOUND);
        }

        // 가게 유효성 검사
        Shop shop = firstMenu.getShop();
        validateShop(shop);

        // 총 금액 계산 & 검증
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart.MenuItem item : cart.getItems()) {
            Menu menu = menus.get(item.getMenuId());
            if (menu == null) {
                throw new InvalidRequestException(ErrorCode.MENU_NOT_FOUND);
            }
            if (!menu.getShop().getId().equals(shop.getId())) {
                throw new InvalidRequestException(ErrorCode.DIFFERENT_SHOP);
            }
            totalAmount = totalAmount.add(
                menu.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        }

        // 최소 주문 금액 확인
        if (totalAmount.compareTo(shop.getMinOrderPrice()) < 0) {
            throw new InvalidRequestException(ErrorCode.MINIMUM_ORDER_NOT_MET);
        }

        return menus;
    }

    private Map<Long, Menu> findMenusByIds(List<Long> menuIds) {
        List<Menu> menus = menuRepository.findByIdIn(menuIds);
        return menus.stream().collect(Collectors.toMap(Menu::getId, Function.identity()));
    }

    private void validateShop(Shop shop) {
        LocalTime now = LocalTime.now();
        if (now.isBefore(shop.getOpen().toLocalTime()) || now.isAfter(
            shop.getClose().toLocalTime())) {
            throw new InvalidRequestException(ErrorCode.SHOP_CLOSED);
        }

        if (shop.isDeleted()) {
            throw new InvalidRequestException(ErrorCode.SHOP_DELETED);
        }
    }
}
