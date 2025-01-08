package com.example.outsourcing.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.outsourcing.domain.cart.entity.Cart;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.order.service.OrderCartValidation;
import com.example.outsourcing.domain.shop.entity.Menu;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.MenuRepository;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderCartValidationTest {

    @InjectMocks
    private OrderCartValidation orderCartValidation;

    @Mock
    private MenuRepository menuRepository;

    @Test
    void validateCartAndReturnMenu_ShouldReturnMenus_유효한_값() {
        Cart cart = new Cart(List.of(new Cart.MenuItem(1L, 2)));
        Menu menu = new Menu(1L, "Test Menu", BigDecimal.valueOf(100),
            new Shop(
                1L,
                "Test Shop",
                BigDecimal.valueOf(50),
                LocalTime.parse("09:00:00"),
                LocalTime.parse("18:00:00"),
                false));

        when(menuRepository.findByIdIn(List.of(1L))).thenReturn(List.of(menu));

        Map<Long, Menu> result = orderCartValidation.validateCartAndReturnMenu(cart);

        assertNotNull(result);
        assertEquals(1, result.size());
    }


    @Test
    void validateCartAndReturnMenu_ShouldThrowException_장바구니가_비어있을때() {
        Cart cart = new Cart(List.of());

        assertThrows(InvalidRequestException.class,
            () -> orderCartValidation.validateCartAndReturnMenu(cart));
    }

    @Test
    void validateCartAndReturnMenu_ShouldThrowException_해당메뉴를_찾을수없을때() {
        Cart cart = new Cart(List.of(new Cart.MenuItem(1L, 2)));

        when(menuRepository.findByIdIn(List.of(1L))).thenReturn(List.of());

        assertThrows(InvalidRequestException.class,
            () -> orderCartValidation.validateCartAndReturnMenu(cart));
    }

    @Test
    void validateCartAndReturnMenu_ShouldThrowException_메뉴별로_가게가_다를때() {
        Cart cart = new Cart(List.of(
            new Cart.MenuItem(1L, 2),
            new Cart.MenuItem(2L, 3)
        ));
        Menu menu1 = new Menu(1L, "Menu 1", BigDecimal.valueOf(10),
            new Shop(1L, "Shop 1", BigDecimal.valueOf(50), LocalTime.parse("09:00:00"),
                LocalTime.parse("18:00:00"), false));
        Menu menu2 = new Menu(2L, "Menu 2", BigDecimal.valueOf(20),
            new Shop(2L, "Shop 2", BigDecimal.valueOf(50), LocalTime.parse("09:00:00"),
                LocalTime.parse("18:00:00"), false));

        when(menuRepository.findByIdIn(List.of(1L, 2L))).thenReturn(List.of(menu1, menu2));

        assertThrows(InvalidRequestException.class,
            () -> orderCartValidation.validateCartAndReturnMenu(cart));
    }

    @Test
    void validateCartAndReturnMenu_ShouldThrowException_최소주문금액보다_작을때() {
        Cart cart = new Cart(List.of(new Cart.MenuItem(1L, 1)));
        Menu menu = new Menu(1L, "Menu 1", BigDecimal.valueOf(10),
            new Shop(1L, "Shop 1", BigDecimal.valueOf(50), LocalTime.parse("09:00:00"),
                LocalTime.parse("18:00:00"), false));

        when(menuRepository.findByIdIn(List.of(1L))).thenReturn(List.of(menu));

        assertThrows(InvalidRequestException.class,
            () -> orderCartValidation.validateCartAndReturnMenu(cart));
    }

    @Test
    void validateCartAndReturnMenu_ShouldThrowException_가게_영업시간이_아닐때() {
        Cart cart = new Cart(List.of(new Cart.MenuItem(1L, 1)));
        Menu menu = new Menu(1L, "Menu 1", BigDecimal.valueOf(10),
            new Shop(1L, "Shop 1", BigDecimal.valueOf(50), LocalTime.parse("09:00:00"),
                LocalTime.parse("18:00:00"), false));

        when(menuRepository.findByIdIn(List.of(1L))).thenReturn(List.of(menu));

        LocalTime now = LocalTime.of(20, 0); // 가게 닫힌 시간에 주문
        try (MockedStatic<LocalTime> mockedTime = Mockito.mockStatic(LocalTime.class)) {
            mockedTime.when(LocalTime::now).thenReturn(now);

            assertThrows(InvalidRequestException.class,
                () -> orderCartValidation.validateCartAndReturnMenu(cart));
        }
    }

    @Test
    void validateCartAndReturnMenu_ShouldThrowException_가게가_폐업했을때() {
        Cart cart = new Cart(List.of(new Cart.MenuItem(1L, 1)));
        Menu menu = new Menu(1L, "Menu 1", BigDecimal.valueOf(10),
            new Shop(1L, "Shop 1", BigDecimal.valueOf(50), LocalTime.parse("09:00:00"),
                LocalTime.parse("18:00:00"), true));

        when(menuRepository.findByIdIn(List.of(1L))).thenReturn(List.of(menu));

        assertThrows(InvalidRequestException.class,
            () -> orderCartValidation.validateCartAndReturnMenu(cart));
    }
}
