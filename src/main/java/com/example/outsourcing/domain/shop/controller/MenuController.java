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

    //메뉴 추가 요청 처리
    @PostMapping("/menus")
    public MenuResponseDto addMenu(
        @RequestBody @Valid MenuRequestDto menuRequestDto, //요청 본문의 메뉴 데이터
        @Auth AuthUser authUser) { // 인증된 사용자 정보
        return menuService.addMenu(authUser, menuRequestDto);
    }

    // 특정 가게의 모든 메뉴 조회 요청 처리
    @GetMapping("/menus")
    public List<MenuResponseDto> getAllMenus(
        @RequestParam Long shopId, // 조회 대상 가게 ID
        @Auth AuthUser authUser) { // 인증된 사용자 정보
        return menuService.getAllMenusByShop(authUser, shopId);
    }

    //특정 메뉴 정보 수정 요청 처리
    @PatchMapping("/menus/{menuId}")
    public MenuResponseDto updateMenu(
        @PathVariable Long menuId, //수정 대상 메뉴 ID
        @RequestBody @Valid MenuRequestDto menuRequestDto, //수정할 메뉴 데이터
        @Auth AuthUser authUser) { // 인증된 사용자 정보
        return menuService.updateMenu(authUser, menuId, menuRequestDto);
    }

    //특정 메뉴 삭제 요청 처리 (소프트 딜리트)
    @DeleteMapping("/menus/{menuId}")
    public void deleteMenu(
        @RequestParam Long shopId, //삭제 대상 메뉴가 속한 가게 ID
        @PathVariable Long menuId, // 삭제 대상 메뉴 ID
        @Auth AuthUser authUser) { // 인증된 사용자 정보
        menuService.softDeleteMenu(authUser, shopId, menuId);
    }
}
