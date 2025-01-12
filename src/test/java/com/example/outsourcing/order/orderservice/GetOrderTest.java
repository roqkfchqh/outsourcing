package com.example.outsourcing.order.orderservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.common.exception.ForbiddenException;
import com.example.outsourcing.domain.order.dto.OrderResponseDto;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.order.entity.OrderMenu;
import com.example.outsourcing.domain.order.repository.OrderRepository;
import com.example.outsourcing.domain.order.service.OrderService;
import com.example.outsourcing.domain.shop.entity.Menu;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.entity.User.UserRole;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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

        Shop shop = new Shop();
        ReflectionTestUtils.setField(shop, "id", 1L);
        ReflectionTestUtils.setField(shop, "name", "Test Shop");
        Menu menu = new Menu();
        ReflectionTestUtils.setField(menu, "id", 1L);
        ReflectionTestUtils.setField(menu, "name", "Test Menu");
        ReflectionTestUtils.setField(menu, "shop", shop);
        OrderMenu orderMenu = new OrderMenu(menu, 2);
        Order order = new Order();
        ReflectionTestUtils.setField(order, "user", User.fromAuthUser(user));
        ReflectionTestUtils.setField(order, "status", Order.Status.PENDING);
        ReflectionTestUtils.setField(order, "orderMenus", List.of(orderMenu));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        OrderResponseDto response = orderService.getOrder(user, orderId);

        assertNotNull(response);
        assertEquals("Test Shop", response.shopName());
    }

    @Test
    void getOrder_ShouldThrowForbiddenException_유효한_유저가_아닐때() {
        // Given
        AuthUser user = new AuthUser(2L, "testghUser", UserRole.USER); // 요청하는 사용자의 ID = 2
        Long orderId = 1L; // 조회하려는 주문 ID

        Shop shop = new Shop();
        ReflectionTestUtils.setField(shop, "id", 1L);
        ReflectionTestUtils.setField(shop, "name", "Test Shop");

        Menu menu = new Menu();
        ReflectionTestUtils.setField(menu, "id", 1L);
        ReflectionTestUtils.setField(menu, "name", "Test Menu");
        ReflectionTestUtils.setField(menu, "shop", shop);

        OrderMenu orderMenu = new OrderMenu(menu, 2);

        Order order = new Order();
        ReflectionTestUtils.setField(order, "user", new User(1L, "owner")); // 주문 소유자 ID = 1
        ReflectionTestUtils.setField(order, "status", Order.Status.PENDING);
        ReflectionTestUtils.setField(order, "orderMenus", List.of(orderMenu));

        // Mock 설정
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.existsOrderByOwner(orderId, user.id())).thenReturn(
            false); // 사용자 2는 소유자가 아님

        // When & Then
        assertThrows(ForbiddenException.class, () -> orderService.getOrder(user, orderId));
    }

}
