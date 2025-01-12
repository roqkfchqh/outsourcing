package com.example.outsourcing.order.orderservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.when;

import com.example.outsourcing.domain.cart.entity.Cart;
import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;
import com.example.outsourcing.domain.order.dto.OrderMenuResponseDto;
import com.example.outsourcing.domain.order.dto.OrderResponseDto;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.order.entity.OrderMenu;
import com.example.outsourcing.domain.order.repository.OrderRepository;
import com.example.outsourcing.domain.order.service.OrderCartService;
import com.example.outsourcing.domain.order.service.OrderCartValidator;
import com.example.outsourcing.domain.order.service.OrderFactory;
import com.example.outsourcing.domain.order.service.OrderService;
import com.example.outsourcing.domain.shop.entity.Menu;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.entity.User.UserRole;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CreateOrderTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderCartService orderCartService;

    @Mock
    private OrderCartValidator orderCartValidator;

    @Mock
    private OrderFactory orderFactory;

    @Mock
    private OrderRepository orderRepository;

    @Test
    void createOrder_ShouldReturnOrderResponseDto_유효한_값() {
        AuthUser authUser = new AuthUser(1L, "Test User", UserRole.USER);
        Cart.MenuItem menuItem = new Cart.MenuItem(1L, 2);
        Cart cart = new Cart(1L, List.of(menuItem));

        User owner = new User();
        ReflectionTestUtils.setField(owner, "id", 1L);
        ReflectionTestUtils.setField(owner, "username", "Shop Owner");

        Shop shop = new Shop();
        ReflectionTestUtils.setField(shop, "id", 1L);
        ReflectionTestUtils.setField(shop, "name", "Test Shop");
        ReflectionTestUtils.setField(shop, "user", owner);

        Menu menu = new Menu();
        ReflectionTestUtils.setField(menu, "id", 1L);
        ReflectionTestUtils.setField(menu, "name", "Test Menu");
        ReflectionTestUtils.setField(menu, "shop", shop);

        OrderMenu orderMenu = new OrderMenu(menu, 2);
        Order order = new Order();
        ReflectionTestUtils.setField(order, "totalPrice", BigDecimal.valueOf(20));
        ReflectionTestUtils.setField(order, "user", User.fromAuthUser(authUser));
        ReflectionTestUtils.setField(order, "status", Order.Status.PENDING);
        ReflectionTestUtils.setField(order, "orderMenus", List.of(orderMenu));

        OrderMenuResponseDto orderMenuResponseDto = new OrderMenuResponseDto(
            "Test Menu",
            2,
            BigDecimal.valueOf(10)
        );
        OrderResponseDto expectedResponse = new OrderResponseDto(
            1L,
            1L,
            2L,
            "Test Shop",
            1L,
            Order.Status.PENDING,
            List.of(orderMenuResponseDto),
            BigDecimal.valueOf(20),
            order.getCreatedAt(),
            order.getUpdatedAt()
        );

        when(orderCartService.getCartData(authUser.id())).thenReturn(cart);
        when(orderCartValidator.validateCartAndReturnMenu(cart)).thenReturn(Map.of(1L, menu));
        when(orderFactory.createOrder(any(User.class), any(BigDecimal.class), anyMap(), anyList()))
            .thenReturn(order);
        when(orderCartValidator.validateShop(any(Long.class), any(BigDecimal.class))).thenReturn(
            shop);

        OrderResponseDto result = orderService.createOrder(authUser);

        assertNotNull(result);
        assertEquals(expectedResponse.shopName(), result.shopName());
        assertEquals(expectedResponse.totalPrice(), result.totalPrice());
    }


    @Test
    void createOrder_ShouldThrowException_장바구니가_비어있을때() {
        AuthUser authUser = new AuthUser(1L, "Test User", UserRole.USER);
        when(orderCartService.getCartData(authUser.id())).thenThrow(
            new InvalidRequestException(ErrorCode.CART_IS_EMPTY));

        assertThrows(InvalidRequestException.class, () -> orderService.createOrder(authUser));
    }

    @Test
    void createOrder_ShouldThrowException_해당메뉴를_찾을수없을때() {
        AuthUser authUser = new AuthUser(1L, "Test User", UserRole.USER);
        Cart cart = new Cart(List.of(new Cart.MenuItem(1L, 2)));
        when(orderCartService.getCartData(authUser.id())).thenReturn(cart);
        when(orderCartValidator.validateCartAndReturnMenu(cart)).thenThrow(
            new InvalidRequestException(ErrorCode.MENU_NOT_FOUND));

        assertThrows(InvalidRequestException.class, () -> orderService.createOrder(authUser));
    }
}
