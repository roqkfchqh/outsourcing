package com.example.outsourcing.domain.shop.service;

import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.common.exception.ForbiddenException;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;
import com.example.outsourcing.domain.shop.Mapper.ShopMapper;
import com.example.outsourcing.domain.shop.dto.ShopRequestDto;
import com.example.outsourcing.domain.shop.dto.ShopResponseDto;
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

    @Transactional
    public ShopResponseDto addShop(AuthUser authUser, ShopRequestDto shopRequestDto) {
        // 사용자당 최대 가게 수 확인
        if (shopRepository.existsByUserIdAndIsDeletedFalse(authUser.id())) {
            throw new InvalidRequestException(ErrorCode.ALREADY_USED_EMAIL);
        }

        User user = userRepository.findById(authUser.id())
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.USER_NOT_FOUND));

        Shop shop = new Shop(user, shopRequestDto.getName(), shopRequestDto.getMinOrderPrice());
        Shop savedShop = shopRepository.save(shop);

        // Mapper를 사용하여 ResponseDto로 변환
        return shopMapper.toResponseDto(savedShop);
    }

    @Transactional
    public ShopResponseDto updateShop(AuthUser authUser, Long shopId,
        ShopRequestDto shopRequestDto) {
        validateOwnership(authUser, shopId);

        Shop shop = shopRepository.findById(shopId)
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.SHOP_NOT_FOUND));

        shop.update(shopRequestDto.getName(), shopRequestDto.getMinOrderPrice());
        Shop updatedShop = shopRepository.save(shop);

        // Mapper를 사용하여 ResponseDto로 변환
        return shopMapper.toResponseDto(updatedShop);
    }

    @Transactional
    public void deleteShop(AuthUser authUser, Long shopId) {
        validateOwnership(authUser, shopId);

        Shop shop = shopRepository.findById(shopId)
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.SHOP_NOT_FOUND));

        shop.markAsDeleted();
        shopRepository.save(shop);
    }

    @Transactional
    public void updateShopHours(AuthUser authUser, Long shopId, LocalTime open, LocalTime close) {
        validateOwnership(authUser, shopId);
        validateShopHours(open, close);

        Shop shop = shopRepository.findById(shopId)
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.SHOP_NOT_FOUND));

        shop.setHours(open, close);
        shopRepository.save(shop);
    }

    public boolean isShopOpen(AuthUser authUser, Long shopId) {
        validateOwnership(authUser, shopId);

        Shop shop = shopRepository.findById(shopId)
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.SHOP_NOT_FOUND));

        if (shop.getOpen() == null || shop.getClose() == null) {
            throw new InvalidRequestException(ErrorCode.SHOP_CLOSED);
        }

        LocalTime now = LocalTime.now();
        return now.isAfter(shop.getOpen()) && now.isBefore(shop.getClose());
    }

    private void validateOwnership(AuthUser authUser, Long shopId) {
        Shop shop = shopRepository.findById(shopId)
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.SHOP_NOT_FOUND));

        if (!shop.getUser().getId().equals(authUser.id())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_OPERATION);
        }
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
