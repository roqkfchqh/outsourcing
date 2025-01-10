package com.example.outsourcing.domain.shop.service;

import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;
import com.example.outsourcing.domain.shop.Mapper.ShopMapper;
import com.example.outsourcing.domain.shop.dto.ShopRequestDto;
import com.example.outsourcing.domain.shop.dto.ShopResponseDto;
import com.example.outsourcing.domain.shop.dto.ShopUpdateRequestDto;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import com.example.outsourcing.domain.user.entity.User;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopService {

    private static final int MAX_SHOPS_PER_USER = 3;

    private final ShopRepository shopRepository;
    private final ShopMapper shopMapper;
    private final ShopMenuValidator validator;

    @Transactional
    public ShopResponseDto addShop(AuthUser authUser,
        ShopRequestDto shopRequestDto) {

        if (shopRepository.existsByName(shopRequestDto.getName())) {
            throw new InvalidRequestException(ErrorCode.SHOP_ALREADY_EXISTS);
        }

        long shopCount = shopRepository.countByUserIdAndIsDeletedFalse(authUser.id());
        if (shopCount >= MAX_SHOPS_PER_USER) {
            throw new InvalidRequestException(ErrorCode.USER_MAX_SHOPS_REACHED); // 예외 처리
        }

        User user = User.fromAuthUser(authUser);

        // ShopService: Shop 생성과 저장
        return shopMapper.toResponseDto(
            shopRepository.save(
                Shop.create(
                    user,
                    shopRequestDto.getName(),
                    shopRequestDto.getMinOrderPrice(),
                    shopRequestDto.getOpen(),
                    shopRequestDto.getClose()
                )
            )
        );
    }

    @Transactional
    public ShopResponseDto updateShop(AuthUser authUser, Long shopId,
        ShopUpdateRequestDto shopUpdateRequestDto) {
        validator.validateOwnership(authUser.id(), shopId);

        Shop shop = validator.findShopByIdOrThrow(shopId);

        if (shopRepository.existsByName(shopUpdateRequestDto.getName())) {
            throw new InvalidRequestException(ErrorCode.SHOP_ALREADY_EXISTS);
        }

        // 가게 이름 업데이트
        if (shopUpdateRequestDto.getName() != null) {
            shop.updateName(shopUpdateRequestDto.getName());
        }

        // 최소 주문 금액 업데이트
        if (shopUpdateRequestDto.getMinOrderPrice() != null) {
            shop.updateMinOrderPrice(shopUpdateRequestDto.getMinOrderPrice());
        }

        // 영업 시간 업데이트
        if (shopUpdateRequestDto.getOpen() != null && shopUpdateRequestDto.getClose() != null) {
            validateShopHours(shopUpdateRequestDto.getOpen(), shopUpdateRequestDto.getClose());
            shop.setHours(shopUpdateRequestDto.getOpen(), shopUpdateRequestDto.getClose());
        }

        // save 호출 없이 바로 DTO 변환
        return shopMapper.toResponseDto(shop);
    }

    @Transactional
    public void deleteShop(AuthUser authUser, Long shopId) {
        validator.validateOwnership(authUser.id(), shopId); // 헬퍼 클래스 활용
        Shop shop = validator.findShopByIdOrThrow(shopId);

        if (shop.isDeleted()) {
            throw new InvalidRequestException(ErrorCode.SHOP_DELETED);
        }

        shop.markAsDeleted();
    }

    private void validateShopHours(LocalTime open, LocalTime close) {
        if (open == null || close == null) {
            throw new InvalidRequestException(ErrorCode.SHOP_CLOSED);
        }
        if (open.isAfter(close)) {
            throw new InvalidRequestException(ErrorCode.CANNOT_CHANGE_STATUS);
        }
        if (open.isBefore(LocalTime.of(6, 0)) || close.isAfter(LocalTime.of(23, 59))) {
            throw new InvalidRequestException(ErrorCode.SHOP_CLOSED);
        }
    }
}
