package com.example.outsourcing.domain.shop.controller;

import com.example.outsourcing.domain.common.annotation.Auth;
import com.example.outsourcing.domain.common.authorization.OwnerCheck;
import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.shop.dto.ShopRequestDto;
import com.example.outsourcing.domain.shop.dto.ShopResponseDto;
import com.example.outsourcing.domain.shop.dto.ShopUpdateRequestDto;
import com.example.outsourcing.domain.shop.service.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    // 가게 추가 요청 처리
    @OwnerCheck
    @PostMapping
    public ResponseEntity<ShopResponseDto> addShop(
        @RequestBody @Valid ShopRequestDto shopRequestDto, // 요청 본문의 가게 데이터
        @Auth AuthUser authUser) { //인증된 사용자 정보
        ShopResponseDto shop = shopService.addShop(authUser, shopRequestDto);
        return ResponseEntity.ok(shop);
    }

    //가게 정보 및 영업 시간 수정 요청 처리
    @OwnerCheck
    @PatchMapping("/{shopId}")
    public ResponseEntity<ShopResponseDto> updateShop(
        @PathVariable Long shopId, //수정 대상 가게 ID
        @RequestBody @Valid ShopUpdateRequestDto shopUpdateRequestDto, //수정할 가게 데이터
        @Auth AuthUser authUser // 인증된 사용자 정보
    ) {
        ShopResponseDto updatedShop = shopService.updateShop(authUser, shopId,
            shopUpdateRequestDto);
        return ResponseEntity.ok(updatedShop);
    }

    //가게 삭제 요청 처리 (소프트 딜리트)
    @OwnerCheck
    @DeleteMapping("/{shopId}")
    public ResponseEntity<Void> deleteShop(
        @PathVariable Long shopId, //삭제 대상 가게 ID
        @Auth AuthUser authUser) { //인증된 사용자 정보
        shopService.deleteShop(authUser, shopId);
        return ResponseEntity.noContent().build();
    }
}
