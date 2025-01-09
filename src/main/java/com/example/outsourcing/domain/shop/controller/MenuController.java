package com.example.outsourcing.domain.shop.controller;

import com.example.outsourcing.domain.common.annotation.Auth;
import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.shop.dto.MenuRequestDto;
import com.example.outsourcing.domain.shop.dto.MenuResponseDto;
import com.example.outsourcing.domain.shop.service.MenuService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
public class MenuController {

    private final MenuService menuService;

    //메뉴 추가
    @PostMapping("/menus")
    public MenuResponseDto addMenu(
        @RequestBody @Valid MenuRequestDto menuRequestDto,
        @Auth AuthUser authUser) {
        return menuService.addMenu(authUser, menuRequestDto);
    }

    // 특정 가게의 모든 메뉴 조회
    @GetMapping("/menus")
    public List<MenuResponseDto> getAllMenus(@RequestParam Long shopId, @Auth AuthUser authUser) {
        return menuService.getAllMenusByShop(authUser, shopId);
    }

    //특정 메뉴 정보 수정
    @PatchMapping("/menus/{menuId}")
    public MenuResponseDto updateMenu(
        @PathVariable Long menuId,
        @RequestBody @Valid MenuRequestDto menuRequestDto,
        @Auth AuthUser authUser) {
        return menuService.updateMenu(authUser, menuId, menuRequestDto);
    }

    @DeleteMapping("/menus/{menuId}")
    public void deleteMenu(
        @RequestParam Long shopId,
        @PathVariable Long menuId,
        @Auth AuthUser authUser) {
        menuService.softDeleteMenu(authUser, shopId, menuId);
    }
}
