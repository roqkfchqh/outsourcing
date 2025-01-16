package com.example.outsourcing.order.orderservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.common.exception.ForbiddenException;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.order.dto.OrderResponseDto;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.order.entity.OrderMenu;
import com.example.outsourcing.domain.order.repository.OrderRepository;
import com.example.outsourcing.domain.order.service.OrderService;
import com.example.outsourcing.domain.shop.entity.Menu;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.entity.User.UserRole;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class GetOrderByShopTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ShopRepository shopRepository;

    @Test
    void getOrdersByShop_ShouldReturnOrderResponses_유효한_값() {
        AuthUser user = new AuthUser(1L, "testOwner", UserRole.OWNER);
        Long shopId = 1L;

        Shop shop = new Shop();
        ReflectionTestUtils.setField(shop, "id", shopId);
        ReflectionTestUtils.setField(shop, "name", "Test Shop");
        ReflectionTestUtils.setField(shop, "isDeleted", false);

        User shopOwner = new User();
        ReflectionTestUtils.setField(shopOwner, "id", 1L);
        ReflectionTestUtils.setField(shopOwner, "username", "Shop Owner");
        ReflectionTestUtils.setField(shop, "user", shopOwner);

        Menu menu = new Menu();
        ReflectionTestUtils.setField(menu, "id", 1L);
        ReflectionTestUtils.setField(menu, "name", "Test Menu");
        ReflectionTestUtils.setField(menu, "price", BigDecimal.TEN);
        ReflectionTestUtils.setField(menu, "shop", shop);

        OrderMenu orderMenu = new OrderMenu(menu, 2);

        Order order = new Order();
        ReflectionTestUtils.setField(order, "user", User.fromAuthUser(user));
        ReflectionTestUtils.setField(order, "status", Order.Status.PENDING);
        ReflectionTestUtils.setField(order, "orderMenus", List.of(orderMenu));

        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        when(orderRepository.findAllByShopId(shopId)).thenReturn(List.of(order));

        List<OrderResponseDto> responses = orderService.getOrdersByShop(user, shopId);

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }


    @Test
    void getOrdersByShop_ShouldThrowInvalidRequestException_폐업한_가게일때() {
        AuthUser user = new AuthUser(1L, "testOwner", UserRole.OWNER);
        Long shopId = 1L;
        Shop shop = new Shop();
        ReflectionTestUtils.setField(shop, "id", shopId);
        ReflectionTestUtils.setField(shop, "name", "Test Shop");
        ReflectionTestUtils.setField(shop, "isDeleted", true);

        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));

        assertThrows(InvalidRequestException.class,
            () -> orderService.getOrdersByShop(user, shopId));
    }

    @Test
    void getOrdersByShop_ShouldThrowForbiddenException_오너가_아닐때() {
        AuthUser user = new AuthUser(2L, "notOwner", UserRole.USER);
        Long shopId = 1L;
        Shop shop = new Shop();
        ReflectionTestUtils.setField(shop, "id", shopId);
        ReflectionTestUtils.setField(shop, "user", new User(1L, "realOwner"));
        ReflectionTestUtils.setField(shop, "name", "Test Shop");

        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));

        assertThrows(ForbiddenException.class,
            () -> orderService.getOrdersByShop(user, shopId));
    }
}
