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
        if (shopRepository.existsByUserIdAndIsDeletedFalse(authUser.id())) {
            throw new InvalidRequestException(ErrorCode.ALREADY_USED_EMAIL);
        }

        User user = userRepository.findById(authUser.id())
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.USER_NOT_FOUND));

        Shop shop = new Shop(user, shopRequestDto.getName(), shopRequestDto.getMinOrderPrice());
        Shop savedShop = shopRepository.save(shop);

        // Mapper 사용
        return shopMapper.toResponseDto(savedShop);
    }

    @Transactional
    public ShopResponseDto updateShop(AuthUser authUser, Long shopId,
        ShopRequestDto shopRequestDto) {
        validateOwnership(authUser, shopId);

        Shop shop = shopRepository.findById(shopId)
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.COMMENT_NOT_FOUND));

        shop.update(shopRequestDto.getName(), shopRequestDto.getMinOrderPrice());
        Shop updatedShop = shopRepository.save(shop);

        // Mapper 사용
        return shopMapper.toResponseDto(updatedShop);
    }

    @Transactional
    public void deleteShop(AuthUser authUser, Long shopId) {
        validateOwnership(authUser, shopId);

        Shop shop = shopRepository.findById(shopId)
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.TODO_NOT_FOUND));

        shop.markAsDeleted();
        shopRepository.save(shop);
    }

    private void validateOwnership(AuthUser authUser, Long shopId) {
        Shop shop = shopRepository.findById(shopId)
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.TODO_NOT_FOUND));

        if (!shop.getUser().getId().equals(authUser.id())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_OPERATION);
        }
    }
}
