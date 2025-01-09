package com.example.outsourcing.domain.shop.controller;

import com.example.outsourcing.domain.common.annotation.Auth;
import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.shop.dto.ShopRequestDto;
import com.example.outsourcing.domain.shop.dto.ShopResponseDto;
import com.example.outsourcing.domain.shop.service.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shops")
public class ShopController {

    private final ShopService shopService;

    @PostMapping
    public ShopResponseDto addShop(
        @RequestBody @Valid ShopRequestDto shopRequestDto,
        @Auth AuthUser authUser) {
        return shopService.addShop(authUser, shopRequestDto);
    }

    @PatchMapping("/{shopId}")
    public ShopResponseDto updateShop(
        @PathVariable Long shopId,
        @RequestBody @Valid ShopRequestDto shopRequestDto,
        @Auth AuthUser authUser
    ) {
        return shopService.updateShop(authUser, shopId, shopRequestDto);
    }

    @DeleteMapping
    public void deleteShop(@PathVariable Long shopId, @Auth AuthUser authUser) {
        shopService.deleteShop(authUser, shopId);
    }
}
