package com.example.outsourcing.domain.shop.service;

import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.common.exception.ForbiddenException;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;
import com.example.outsourcing.domain.shop.Mapper.MenuMapper;
import com.example.outsourcing.domain.shop.dto.MenuRequestDto;
import com.example.outsourcing.domain.shop.dto.MenuResponseDto;
import com.example.outsourcing.domain.shop.entity.Menu;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.MenuRepository;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final ShopRepository shopRepository;
    private final MenuMapper menuMapper;

    @Transactional
    public MenuResponseDto addMenu(AuthUser authUser, MenuRequestDto menuRequestDto) {
        validateOwnership(authUser, menuRequestDto.getShopId());

        if (menuRepository.existsByShopIdAndNameAndIsDeletedFalse(menuRequestDto.getShopId(),
            menuRequestDto.getName())) {
            throw new InvalidRequestException(ErrorCode.ALREADY_USED_EMAIL);
        }

        Shop shop = shopRepository.findById(menuRequestDto.getShopId())
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.TODO_NOT_FOUND));
        Menu menu = new Menu(shop, menuRequestDto.getName(), menuRequestDto.getDescription(),
            menuRequestDto.getPrice());
        Menu savedMenu = menuRepository.save(menu);

        // Mapper 사용
        return menuMapper.toResponseDto(savedMenu);
    }

    public List<MenuResponseDto> getAllMenusByShop(AuthUser authUser, Long shopId) {
        validateOwnership(authUser, shopId);

        // Mapper 사용
        return menuRepository.findByShopIdAndIsDeletedFalse(shopId).stream()
            .map(menuMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public MenuResponseDto updateMenu(AuthUser authUser, Long menuId,
        MenuRequestDto menuRequestDto) {
        validateOwnership(authUser, menuRequestDto.getShopId());

        Menu menu = menuRepository.findById(menuId)
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.TODO_NOT_FOUND));

        menu.update(menuRequestDto.getName(), menuRequestDto.getDescription(),
            menuRequestDto.getPrice());
        Menu savedMenu = menuRepository.save(menu);

        // Mapper 사용
        return menuMapper.toResponseDto(savedMenu);
    }

    @Transactional
    public void softDeleteMenu(AuthUser authUser, Long shopId, Long menuId) {
        validateOwnership(authUser, shopId);

        Menu menu = menuRepository.findById(menuId)
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.TODO_NOT_FOUND));

        menu.markAsDeleted();
        menuRepository.save(menu);
    }

    private void validateOwnership(AuthUser authUser, Long shopId) {
        Shop shop = shopRepository.findById(shopId)
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.TODO_NOT_FOUND));

        if (!shop.getUser().getId().equals(authUser.id())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_OPERATION);
        }
    }
}
