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

    // 가게 추가 요청 처리
    @PostMapping
    public ShopResponseDto addShop(
        @RequestBody @Valid ShopRequestDto shopRequestDto, // 요청 본문의 가게 데이터
        @Auth AuthUser authUser) { //인증된 사용자 정보
        return shopService.addShop(authUser, shopRequestDto);
    }

    //가게 정보 수정 요청 처리
    @PatchMapping("/{shopId}")
    public ShopResponseDto updateShop(
        @PathVariable Long shopId, //수정 대상 가게 ID
        @RequestBody @Valid ShopRequestDto shopRequestDto, //수정할 가게 데이터
        @Auth AuthUser authUser // 인증된 사용자 정보
    ) {
        return shopService.updateShop(authUser, shopId, shopRequestDto);
    }

    //특정 가게의 영업 상태 확인
    @GetMapping("/{shopId}/is-open")
    public boolean isShopOpen(
        @PathVariable Long shopId, //확안 대상 가게 ID
        @Auth AuthUser authUser) { //인증된 사용자 정보
        return shopService.isShopOpen(authUser, shopId);
    }

    //가게 영업 시간 업데이트 요청 처리
    @PatchMapping("/{shopId}/hours")
    public void updateShopHours(
        @PathVariable Long shopId, //영업 시간 설정 대상 가게 ID
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime open, //영업 시작 시간
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime close, //영업 종료 시간
        @Auth AuthUser authUser) { //인증된 사용자 정보
        shopService.updateShopHours(authUser, shopId, open, close);
    }

    //가게 삭제 요청 처리 (소프트 딜리트)
    @DeleteMapping
    public void deleteShop(
        @PathVariable Long shopId, //삭제 대상 가게 ID
        @Auth AuthUser authUser) { //인증된 사용자 정보
        shopService.deleteShop(authUser, shopId);
    }
}
