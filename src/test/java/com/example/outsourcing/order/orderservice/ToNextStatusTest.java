package com.example.outsourcing.order.orderservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.common.exception.ForbiddenException;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.order.entity.Order.Status;
import com.example.outsourcing.domain.order.repository.OrderRepository;
import com.example.outsourcing.domain.order.service.OrderService;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.entity.User.UserRole;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ToNextStatusTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Test
    void toNextStatus_ShouldUpdateOrderStatus_WhenUserIsOwner() {
        AuthUser user = new AuthUser(1L, "testUser", UserRole.OWNER);
        Long orderId = 1L;

        Order order = new Order(User.fromAuthUser(user), Status.PENDING);   //Pending에서 accept로만 해봤음
        when(orderRepository.findOrderByOwner(orderId, user.id())).thenReturn(Optional.of(order));

        orderService.toNextStatus(user, orderId);

        verify(orderRepository).findOrderByOwner(orderId, user.id());
        assertEquals(Status.ACCEPT, order.getStatus());
    }

    @Test
    void toNextStatus_ShouldThrowForbiddenException_WhenUserIsNotOwner() {
        AuthUser user = new AuthUser(1L, "testUser", UserRole.USER);
        Long orderId = 1L;

        when(orderRepository.findOrderByOwner(orderId, user.id())).thenReturn(Optional.empty());

        assertThrows(ForbiddenException.class, () -> orderService.toNextStatus(user, orderId));
    }
}
