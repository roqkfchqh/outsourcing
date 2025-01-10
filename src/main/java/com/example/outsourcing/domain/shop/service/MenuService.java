package com.example.outsourcing.domain.shop.service;

import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;
import com.example.outsourcing.domain.shop.Mapper.MenuMapper;
import com.example.outsourcing.domain.shop.dto.MenuRequestDto;
import com.example.outsourcing.domain.shop.dto.MenuResponseDto;
import com.example.outsourcing.domain.shop.entity.Menu;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.MenuRepository;
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
    private final MenuMapper menuMapper;
    private final ShopMenuValidator validator;

    @Transactional
    public MenuResponseDto addMenu(AuthUser authUser,
        MenuRequestDto menuRequestDto, Long shopId) {
        validator.validateOwnership(authUser.id(), shopId);

        // 중복된 메뉴 이름 확인
        if (menuRepository.existsByShopIdAndNameAndIsDeletedFalse(shopId,
            menuRequestDto.getName())) {
            throw new InvalidRequestException(ErrorCode.MENU_ALREADY_EXISTS); // 중복 메뉴 처리
        }

        Shop shop = validator.findShopByIdOrThrow(shopId); // 가게 존재 여부
        // MenuService: Menu 생성과 저장
        return menuMapper.toResponseDto(
            menuRepository.save(
                Menu.create(shop, menuRequestDto.getName(), menuRequestDto.getDescription(),
                    menuRequestDto.getPrice())
            )
        );
    }

    public List<MenuResponseDto> getAllMenusByShop(AuthUser authUser, Long shopId) {
        validator.validateOwnership(authUser.id(), shopId);

        // Mapper를 사용하여 ResponseDto 리스트로 변환
        return menuRepository.findByShopIdAndIsDeletedFalse(shopId).stream()
            .map(menuMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public MenuResponseDto updateMenu(AuthUser authUser, Long menuId,
        MenuRequestDto menuRequestDto, Long shopId) {
        validator.validateOwnership(authUser.id(), shopId);

        // 메뉴 존재 여부 확인
        Menu menu = validator.findMenuByIdOrThrow(menuId);

        menu.update(menuRequestDto.getName(), menuRequestDto.getDescription(),
            menuRequestDto.getPrice());

        // Mapper를 사용하여 ResponseDto로 변환
        return menuMapper.toResponseDto(menu);
    }

    @Transactional
    public void softDeleteMenu(AuthUser authUser, Long shopId, Long menuId) {
        validator.validateOwnership(authUser.id(), shopId);

        // 메뉴 존재 여부 확인
        Menu menu = validator.findMenuByIdOrThrow(menuId);

        menu.markAsDeleted();
    }
}
