package com.example.outsourcing.order.orderservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.common.exception.ForbiddenException;
import com.example.outsourcing.domain.order.dto.OrderResponseDto;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.order.entity.Order.Status;
import com.example.outsourcing.domain.order.entity.OrderMenu;
import com.example.outsourcing.domain.order.repository.OrderRepository;
import com.example.outsourcing.domain.order.service.OrderService;
import com.example.outsourcing.domain.shop.entity.Menu;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.entity.User.UserRole;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetOrderTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Test
    void getOrder_ShouldReturnOrderResponse_유효한_값() {
        AuthUser user = new AuthUser(1L, "testUser", UserRole.USER);
        Long orderId = 1L;
        Shop shop = new Shop(1L, "Test Shop", BigDecimal.valueOf(50), LocalTime.parse("09:00:00"),
            LocalTime.parse("18:00:00"), false);
        Menu menu = new Menu(1L, "Test Menu", BigDecimal.TEN, shop);
        OrderMenu orderMenu = OrderMenu.of(menu, 2);
        Order order = new Order(User.fromAuthUser(user), Status.PENDING, List.of(orderMenu));
        orderMenu.assignOrder(order);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        OrderResponseDto response = orderService.getOrder(user, orderId);

        assertNotNull(response);
        assertEquals("Test Shop", response.shopName());
    }

    @Test
    void getOrder_ShouldThrowForbiddenException_WhenUserIsUnauthorized() {
        AuthUser user = new AuthUser(2L, "tesghUser", UserRole.USER);
        Long orderId = 1L;
        Shop shop = new Shop(1L, "Test Shop", BigDecimal.valueOf(50), LocalTime.parse("09:00:00"),
            LocalTime.parse("18:00:00"), false);
        Menu menu = new Menu(1L, "Test Menu", BigDecimal.TEN, shop);
        OrderMenu orderMenu = OrderMenu.of(menu, 2);
        Order order = new Order(new User(), Status.PENDING, List.of(orderMenu));
        orderMenu.assignOrder(order);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.existsOrderByOwner(orderId, user.id())).thenReturn(false);

        assertThrows(ForbiddenException.class, () -> orderService.getOrder(user, orderId));
    }

}
