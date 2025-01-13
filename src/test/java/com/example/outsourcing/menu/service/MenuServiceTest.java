package com.example.outsourcing.menu.service;

import static org.hibernate.internal.util.ExceptionHelper.doThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;
import com.example.outsourcing.domain.shop.dto.MenuRequestDto;
import com.example.outsourcing.domain.shop.dto.MenuResponseDto;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.MenuRepository;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import com.example.outsourcing.domain.shop.service.MenuService;
import com.example.outsourcing.domain.shop.service.ShopMenuValidator;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.entity.User.UserRole;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {


    @InjectMocks
    private MenuService menuService;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private ShopMenuValidator validator;

    @Mock
    private ShopRepository shopRepository;

    @Test
    void shouldAddMenu_WhenValidRequest() {
        // given
        Shop shop = createTestShop(1L, "Test Shop", false);
        when(shopRepository.findById(shop.getId())).thenReturn(Optional.of(shop));
        when(menuRepository.existsByShopIdAndNameAndIsDeletedFalse(shop.getId(), "New Menu"))
            .thenReturn(false);

        MenuRequestDto menuRequestDto = new MenuRequestDto("New Menu", "Description",
            BigDecimal.TEN, shop.getId());

        // when
        MenuResponseDto response = menuService.addMenu(
            new AuthUser(1L, "owner@test.com", UserRole.OWNER),
            menuRequestDto, shop.getId());

        // then
        assertNotNull(response);
        assertEquals("New Menu", response.getName());
        assertEquals(BigDecimal.TEN, response.getPrice());
    }

    @Test
    void shouldThrowException_WhenAddingMenuToDeletedShop() {
        // given
        Shop shop = createTestShop(1L, "Closed Shop", true);
        when(shopRepository.findById(shop.getId())).thenReturn(Optional.of(shop));

        MenuRequestDto menuRequestDto = new MenuRequestDto("Menu", "Description",
            BigDecimal.TEN, shop.getId());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
            () -> menuService.addMenu(new AuthUser(1L, "owner@test.com", UserRole.OWNER),
                menuRequestDto, shop.getId()));

        assertEquals("The shop is closed.", exception.getMessage());
    }

    @Test
    void shouldThrowException_WhenAddingDuplicateMenuName() {
        // given
        Shop shop = createTestShop(1L, "Test Shop", false);
        when(shopRepository.findById(shop.getId())).thenReturn(Optional.of(shop));
        when(menuRepository.existsByShopIdAndNameAndIsDeletedFalse(shop.getId(), "Duplicate Menu"))
            .thenReturn(true);

        MenuRequestDto menuRequestDto = new MenuRequestDto("Duplicate Menu", "Description",
            BigDecimal.TEN, shop.getId());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
            () -> menuService.addMenu(new AuthUser(1L, "owner@test.com", UserRole.OWNER),
                menuRequestDto, shop.getId()));

        assertEquals("Menu with the same name already exists.", exception.getMessage());
    }

    @Test
    void shouldThrowException_WhenUserIsNotOwner() {
        // given
        Shop shop = createTestShop(1L, "Test Shop", false);
        when(shopRepository.findById(shop.getId())).thenReturn(Optional.of(shop));
        when(validator.validateOwnership(2L, shop.getId()));
        // Mocking: validateOwnership 메서드가 InvalidRequestException을 던지도록 설정
        doThrow(new InvalidRequestException(ErrorCode.FORBIDDEN_OWNER)
            .when(validator).validateOwnership(2L, shop.getId()));

        MenuRequestDto menuRequestDto = new MenuRequestDto("Test Menu", "Description",
            BigDecimal.TEN, shop.getId());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
            () -> menuService.addMenu(new AuthUser(2L, "notOwner@test.com", UserRole.USER),
                menuRequestDto, shop.getId()));

        assertEquals("You are not the owner of this shop.", exception.getMessage());
    }

    private Shop createTestShop(Long id, String name, boolean isDeleted) {
        return new Shop(id, new User(1L, "Owner", "owner@test.com", UserRole.OWNER), name,
            BigDecimal.valueOf(50),
            LocalTime.of(9, 0), LocalTime.of(18, 0), isDeleted);
    }
}
