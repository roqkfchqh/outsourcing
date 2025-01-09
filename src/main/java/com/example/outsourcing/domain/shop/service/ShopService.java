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
import com.example.outsourcing.domain.user.repository.UserRepository;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopService {

    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final ShopMapper shopMapper;
    private final ShopMenuValidator validator;

    @Transactional
    public ShopResponseDto addShop(AuthUser authUser,
        ShopRequestDto shopRequestDto) {
        // 사용자당 최대 가게 수 확인
        if (shopRepository.existsByUserIdAndIsDeletedFalse(authUser.id())) {
            throw new InvalidRequestException(ErrorCode.ALREADY_USED_EMAIL);
        }

        User user = User.fromAuthUser(authUser);

        Shop shop = new Shop(user, shopRequestDto.getName(), shopRequestDto.getMinOrderPrice());
        Shop savedShop = shopRepository.save(shop);

        // Shop 객체 생성 및 저장 후 바로 DTO 변환
        return shopMapper.toResponseDto(
            shopRepository.save(
                new Shop(user, shopRequestDto.getName(), shopRequestDto.getMinOrderPrice())
            )
        );
    }

    @Transactional
    public ShopResponseDto updateShop(AuthUser authUser, Long shopId,
        ShopUpdateRequestDto shopUpdateRequestDto) {
        validator.validateOwnership(authUser.id(), shopId);

        Shop shop = validator.findShopByIdOrThrow(shopId);

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
