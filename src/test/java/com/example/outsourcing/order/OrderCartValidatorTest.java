package com.example.outsourcing.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.outsourcing.domain.cart.entity.Cart;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.order.service.OrderCartValidator;
import com.example.outsourcing.domain.shop.entity.Menu;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.MenuRepository;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OrderCartValidatorTest {

    @InjectMocks
    private OrderCartValidator orderCartValidator;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private ShopRepository shopRepository;

    @Test
    void validateCartAndReturnMenu_ShouldReturnMenus_유효한_값() {
        Cart cart = new Cart(List.of(new Cart.MenuItem(1L, 2)));
        Menu menu = new Menu();
        ReflectionTestUtils.setField(menu, "id", 1L);
        ReflectionTestUtils.setField(menu, "name", "Test Menu");
        ReflectionTestUtils.setField(menu, "price", BigDecimal.valueOf(100));
        ReflectionTestUtils.setField(menu, "shop", new Shop());

        when(menuRepository.findByIdIn(List.of(1L))).thenReturn(List.of(menu));

        Map<Long, Menu> result = orderCartValidator.validateCartAndReturnMenu(cart);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void validateCartAndReturnMenu_ShouldThrowException_해당메뉴를_찾을수없을때() {
        Cart cart = new Cart(List.of(new Cart.MenuItem(1L, 2)));

        when(menuRepository.findByIdIn(List.of(1L))).thenReturn(List.of());

        assertThrows(InvalidRequestException.class,
            () -> orderCartValidator.validateCartAndReturnMenu(cart));
    }

    @Test
    void validateShop_ShouldReturnShop_유효한_값() {
        Long shopId = 1L;
        BigDecimal totalPrice = BigDecimal.valueOf(100);

        Shop shop = new Shop();
        ReflectionTestUtils.setField(shop, "id", shopId);
        ReflectionTestUtils.setField(shop, "name", "Test Shop");
        ReflectionTestUtils.setField(shop, "minOrderPrice", BigDecimal.valueOf(50));
        ReflectionTestUtils.setField(shop, "isDeleted", false);
        ReflectionTestUtils.setField(shop, "open", LocalTime.of(0, 0));
        ReflectionTestUtils.setField(shop, "close", LocalTime.of(23, 59));

        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));

        Shop result = orderCartValidator.validateShop(shopId, totalPrice);

        assertNotNull(result);
        assertEquals(shopId, result.getId());
        assertEquals("Test Shop", result.getName());
    }


    @Test
    void validateShop_ShouldThrowException_가게를_찾을수없을때() {
        Long shopId = 1L;
        BigDecimal totalPrice = BigDecimal.valueOf(100);

        when(shopRepository.findById(shopId)).thenReturn(Optional.empty());

        assertThrows(InvalidRequestException.class,
            () -> orderCartValidator.validateShop(shopId, totalPrice));
    }

    @Test
    void validateShop_ShouldThrowException_가게가_폐업했을때() {
        Long shopId = 1L;
        BigDecimal totalPrice = BigDecimal.valueOf(100);
        Shop shop = new Shop();
        ReflectionTestUtils.setField(shop, "id", shopId);
        ReflectionTestUtils.setField(shop, "name", "Test Shop");
        ReflectionTestUtils.setField(shop, "isDeleted", true);
        ReflectionTestUtils.setField(shop, "open", LocalTime.of(9, 0));
        ReflectionTestUtils.setField(shop, "close", LocalTime.of(18, 0));

        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));

        assertThrows(InvalidRequestException.class,
            () -> orderCartValidator.validateShop(shopId, totalPrice));
    }

    @Test
    void validateShop_ShouldThrowException_최소주문금액_미달일때() {
        Long shopId = 1L;
        BigDecimal totalPrice = BigDecimal.valueOf(30); // 최소 주문 금액 미달
        Shop shop = new Shop();
        ReflectionTestUtils.setField(shop, "id", shopId);
        ReflectionTestUtils.setField(shop, "name", "Test Shop");
        ReflectionTestUtils.setField(shop, "minOrderPrice", BigDecimal.valueOf(50));
        ReflectionTestUtils.setField(shop, "isDeleted", false);
        ReflectionTestUtils.setField(shop, "open", LocalTime.of(9, 0));
        ReflectionTestUtils.setField(shop, "close", LocalTime.of(18, 0));

        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));

        assertThrows(InvalidRequestException.class,
            () -> orderCartValidator.validateShop(shopId, totalPrice));
    }

    @Test
    void validateShop_ShouldThrowException_영업시간이_아닐때() {
        Long shopId = 1L;
        BigDecimal totalPrice = BigDecimal.valueOf(100);

        Shop shop = new Shop();
        ReflectionTestUtils.setField(shop, "id", shopId);
        ReflectionTestUtils.setField(shop, "name", "Test Shop");
        ReflectionTestUtils.setField(shop, "minOrderPrice", BigDecimal.valueOf(50));
        ReflectionTestUtils.setField(shop, "isDeleted", false);
        ReflectionTestUtils.setField(shop, "open", LocalTime.of(9, 0));
        ReflectionTestUtils.setField(shop, "close", LocalTime.of(10, 0));

        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));

        assertThrows(InvalidRequestException.class,
            () -> orderCartValidator.validateShop(shopId, totalPrice));
    }

}
