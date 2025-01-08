package com.example.outsourcing.domain.order.service;

import com.example.outsourcing.domain.cart.entity.Cart;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderCartService {

    private final CacheManager cacheManager;

    public Cart getCartData(Long userId) {
        Cache cache = cacheManager.getCache("carts");
        if (cache == null) {
            throw new InvalidRequestException(ErrorCode.CART_IS_EMPTY);
        }
        Cart cart = cache.get(userId, Cart.class);
        if (cart == null || cart.getItems().isEmpty()) {
            throw new InvalidRequestException(ErrorCode.CART_IS_EMPTY);
        }
        return cart;
    }

    public void evictCartData(Long userId) {
        Cache cache = cacheManager.getCache("carts");
        if (cache != null) {
            cache.evict(userId);
        }
    }
}
