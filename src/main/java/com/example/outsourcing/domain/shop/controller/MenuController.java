package com.example.outsourcing.domain.shop.controller;

import com.example.outsourcing.domain.common.annotation.Auth;
import com.example.outsourcing.domain.common.authorization.OwnerCheck;
import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.common.dto.BaseMapper;
import com.example.outsourcing.domain.common.dto.BaseResponseDto;
import com.example.outsourcing.domain.common.dto.MessageResponseDto;
import com.example.outsourcing.domain.shop.dto.MenuRequestDto;
import com.example.outsourcing.domain.shop.dto.MenuResponseDto;
import com.example.outsourcing.domain.shop.service.MenuService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @OwnerCheck
    @PostMapping("/menus")
    public ResponseEntity<BaseResponseDto<MenuResponseDto>> addMenu(
        @RequestBody @Valid MenuRequestDto menuRequestDto, //요청 본문의 메뉴 데이터
        @RequestParam Long shopId,
        @Auth AuthUser authUser) { // 인증된 사용자 정보
        MenuResponseDto menu = menuService.addMenu(authUser, menuRequestDto, shopId);
        return ResponseEntity.ok(BaseMapper.map(menu));
    }

    // 특정 가게의 모든 메뉴 조회 요청 처리
    @GetMapping("/menus")
    public ResponseEntity<BaseResponseDto<List<MenuResponseDto>>> getAllMenus(
        @RequestParam Long shopId, // 조회 대상 가게 ID
        @Auth AuthUser authUser) { // 인증된 사용자 정보
        List<MenuResponseDto> menus = menuService.getAllMenusByShop(authUser, shopId);
        return ResponseEntity.ok(BaseMapper.map(menus));
    }

    //특정 메뉴 정보 수정 요청 처리
    @OwnerCheck
    @PatchMapping("/menus/{menuId}")
    public ResponseEntity<BaseResponseDto<MenuResponseDto>> updateMenu(
        @PathVariable Long menuId, //수정 대상 메뉴 ID
        @RequestBody @Valid MenuRequestDto menuRequestDto, //수정할 메뉴 데이터
        @RequestParam Long shopId,
        @Auth AuthUser authUser) { // 인증된 사용자 정보
        MenuResponseDto updatedMenu = menuService.updateMenu(authUser, menuId, menuRequestDto,
            shopId);
        return ResponseEntity.ok(BaseMapper.map(updatedMenu));
    }

    //특정 메뉴 삭제 요청 처리 (소프트 딜리트)
    @OwnerCheck
    @DeleteMapping("/menus/{menuId}")
    public ResponseEntity<BaseResponseDto<MessageResponseDto>> deleteMenu(
        @RequestParam Long shopId, //삭제 대상 메뉴가 속한 가게 ID
        @PathVariable Long menuId, // 삭제 대상 메뉴 ID
        @Auth AuthUser authUser) { // 인증된 사용자 정보
        menuService.softDeleteMenu(authUser, shopId, menuId);
        MessageResponseDto data = new MessageResponseDto(menuId + " 번 메뉴가 삭제되었습니다.");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(BaseMapper.map(data));
    }
}
