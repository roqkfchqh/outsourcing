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

        // 중복된 메뉴 이름 확인
        if (menuRepository.existsByShopIdAndNameAndIsDeletedFalse(menuRequestDto.getShopId(),
            menuRequestDto.getName())) {
            throw new InvalidRequestException(ErrorCode.MENU_ALREADY_EXISTS); // 중복 메뉴 처리
        }

        Shop shop = shopRepository.findById(menuRequestDto.getShopId())
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.SHOP_NOT_FOUND)); // 가게 존재 여부
        Menu menu = new Menu(shop, menuRequestDto.getName(), menuRequestDto.getDescription(),
            menuRequestDto.getPrice());
        Menu savedMenu = menuRepository.save(menu);

        // Mapper를 사용하여 ResponseDto로 변환
        return menuMapper.toResponseDto(savedMenu);
    }

    public List<MenuResponseDto> getAllMenusByShop(AuthUser authUser, Long shopId) {
        validateOwnership(authUser, shopId);

        // Mapper를 사용하여 ResponseDto 리스트로 변환
        return menuRepository.findByShopIdAndIsDeletedFalse(shopId).stream()
            .map(menuMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public MenuResponseDto updateMenu(AuthUser authUser, Long menuId,
        MenuRequestDto menuRequestDto) {
        validateOwnership(authUser, menuRequestDto.getShopId());

        // 메뉴 존재 여부 확인
        Menu menu = menuRepository.findById(menuId)
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.MENU_NOT_FOUND));

        menu.update(menuRequestDto.getName(), menuRequestDto.getDescription(),
            menuRequestDto.getPrice());
        Menu savedMenu = menuRepository.save(menu);

        // Mapper를 사용하여 ResponseDto로 변환
        return menuMapper.toResponseDto(savedMenu);
    }

    @Transactional
    public void softDeleteMenu(AuthUser authUser, Long shopId, Long menuId) {
        validateOwnership(authUser, shopId);

        // 메뉴 존재 여부 확인
        Menu menu = menuRepository.findById(menuId)
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.MENU_NOT_FOUND));

        menu.markAsDeleted();
        menuRepository.save(menu);
    }

    private void validateOwnership(AuthUser authUser, Long shopId) {
        // 가게 존재 여부 및 소유 확인
        Shop shop = shopRepository.findById(shopId)
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.SHOP_NOT_FOUND));

        if (!shop.getUser().getId().equals(authUser.id())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_OPERATION);
        }
    }
}
