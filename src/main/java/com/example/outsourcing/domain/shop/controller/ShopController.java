package com.example.outsourcing.domain.shop.controller;

import com.example.outsourcing.domain.common.annotation.Auth;
import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.shop.dto.ShopRequestDto;
import com.example.outsourcing.domain.shop.dto.ShopResponseDto;
import com.example.outsourcing.domain.shop.service.ShopService;
import jakarta.validation.Valid;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/{shopId}/is-open")
    public boolean isShopOpen(@PathVariable Long shopId, @Auth AuthUser authUser) {
        return shopService.isShopOpen(authUser, shopId);
    }

    @PatchMapping("/{shopId}/hours")
    public void updateShopHours(
        @PathVariable Long shopId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime open,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime close,
        @Auth AuthUser authUser) {
        shopService.updateShopHours(authUser, shopId, open, close);
    }

    @DeleteMapping
    public void deleteShop(@PathVariable Long shopId, @Auth AuthUser authUser) {
        shopService.deleteShop(authUser, shopId);
    }
}
