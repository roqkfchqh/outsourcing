package com.example.outsourcing.domain.cart.controller;

import com.example.outsourcing.domain.cart.entity.Cart;
import com.example.outsourcing.domain.cart.service.CartService;
import com.example.outsourcing.domain.common.annotation.Auth;
import com.example.outsourcing.domain.common.authorization.UserCheck;
import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.common.dto.BaseMapper;
import com.example.outsourcing.domain.common.dto.BaseResponseDto;
import com.example.outsourcing.domain.common.dto.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    @UserCheck
    @GetMapping("/items")
    public ResponseEntity<BaseResponseDto<Cart>> getCart(@Auth AuthUser authUser) {
        Cart cart = cartService.getCart(authUser);

        return ResponseEntity.ok(BaseMapper.map(cart));
    }

    @UserCheck
    @PostMapping("/items/{menuId}")
    public ResponseEntity<BaseResponseDto<Cart>> addItemToCart(@Auth AuthUser authUser,
        @PathVariable Long menuId) {
        Cart cart = cartService.addItemToCart(authUser, menuId);

        return ResponseEntity.ok(BaseMapper.map(cart));
    }

    @UserCheck
    @DeleteMapping("/items/{menuId}")
    public ResponseEntity<BaseResponseDto<Cart>> removeItemFromCart(@Auth AuthUser authUser,
        @PathVariable Long menuId) {
        Cart cart = cartService.removeItemFromCart(authUser, menuId);

        return ResponseEntity.ok(BaseMapper.map(cart));
    }

    @UserCheck
    @DeleteMapping("/items")
    public ResponseEntity<BaseResponseDto<MessageResponseDto>> clearCart(@Auth AuthUser authUser) {
        cartService.clearCart(authUser);
        MessageResponseDto data = new MessageResponseDto(authUser.email() + " 님의 장바구니가 초기화되었습니다.");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(BaseMapper.map(data));
    }
}
