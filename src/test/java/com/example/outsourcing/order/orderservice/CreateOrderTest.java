package com.example.outsourcing.order.orderservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

import com.example.outsourcing.domain.cart.entity.Cart;
import com.example.outsourcing.domain.cart.entity.OrderMenu;
import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;
import com.example.outsourcing.domain.order.dto.OrderMenuResponseDto;
import com.example.outsourcing.domain.order.dto.OrderResponseDto;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.order.repository.OrderRepository;
import com.example.outsourcing.domain.order.service.OrderCartService;
import com.example.outsourcing.domain.order.service.OrderCartValidation;
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

@ExtendWith(MockitoExtension.class)
class CreateOrderTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderCartService orderCartService;

    @Mock
    private OrderCartValidation orderCartValidation;

    @Mock
    private OrderFactory orderFactory;

    @Mock
    private OrderRepository orderRepository;

    @Test
    void createOrder_ShouldReturnOrderResponseDto_유효한_값() {
        AuthUser authUser = new AuthUser(1L, "Test User", UserRole.USER);
        Cart.MenuItem menuItem = new Cart.MenuItem(1L, 2);
        Cart cart = new Cart(List.of(menuItem));
        Shop shop = new Shop(1L, "Test Shop", BigDecimal.valueOf(50), null, null, false);
        Menu menu = new Menu(1L, "Test Menu", BigDecimal.valueOf(10), shop);
        OrderMenu orderMenu = new OrderMenu(menu, 2);
        Order order = new Order(
            User.fromAuthUser(authUser),
            Order.Status.PENDING,
            List.of(orderMenu));
        OrderMenuResponseDto orderMenuResponseDto = new OrderMenuResponseDto(
            "Test Menu",
            2,
            BigDecimal.valueOf(10));
        OrderResponseDto expectedResponse = new OrderResponseDto(
            "Test Shop",
            "Test User",
            Order.Status.PENDING,
            List.of(orderMenuResponseDto),
            BigDecimal.valueOf(20)
        );

        when(orderCartService.getCartData(authUser.id())).thenReturn(cart);
        when(orderCartValidation.validateCartAndReturnMenu(cart)).thenReturn(Map.of(1L, menu));
        when(orderFactory.createOrder(any(User.class), anyMap(), anyList())).thenReturn(order);

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
        when(orderCartValidation.validateCartAndReturnMenu(cart)).thenThrow(
            new InvalidRequestException(ErrorCode.MENU_NOT_FOUND));

        assertThrows(InvalidRequestException.class, () -> orderService.createOrder(authUser));
    }
}
