package com.example.outsourcing.domain.shop.service;

import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;
import com.example.outsourcing.domain.shop.entity.Menu;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.MenuRepository;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShopMenuValidator {

    private final ShopRepository shopRepository;
    private final MenuRepository menuRepository;

    // Shop을 ID로 조회하거나 예외 발생
    public Shop findShopByIdOrThrow(Long shopId) {
        return shopRepository.findById(shopId)
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.SHOP_NOT_FOUND));
    }

    // Menu를 ID로 조회하거나 예외 발생
    public Menu findMenuByIdOrThrow(Long menuId) {
        return menuRepository.findById(menuId)
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.MENU_NOT_FOUND));
    }

    // 가게 소유자 확인
    public void validateOwnership(Long userId, Long shopId) {
        Shop shop = findShopByIdOrThrow(shopId);
        if (!shop.getUser().getId().equals(userId)) {
            throw new InvalidRequestException(ErrorCode.FORBIDDEN_OPERATION);
        }
    }

}
