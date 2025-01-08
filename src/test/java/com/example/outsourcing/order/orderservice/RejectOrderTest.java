package com.example.outsourcing.order.orderservice;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
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
public class RejectOrderTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Test
    void rejectOrder_ShouldDeleteOrder_WhenStatusIsPending() {
        AuthUser user = new AuthUser(1L, "testUser", UserRole.OWNER);
        Long orderId = 1L;

        Order order = new Order(User.fromAuthUser(user), Status.PENDING);
        when(orderRepository.findOrderByOwner(orderId, user.id())).thenReturn(Optional.of(order));

        orderService.rejectOrder(user, orderId);

        verify(orderRepository).deleteById(orderId);
    }

    @Test
    void rejectOrder_ShouldThrowException_WhenStatusIsNotPending() {
        AuthUser user = new AuthUser(1L, "testUser", UserRole.OWNER);
        Long orderId = 1L;

        Order order = new Order(User.fromAuthUser(user), Status.ACCEPT);
        when(orderRepository.findOrderByOwner(orderId, user.id())).thenReturn(Optional.of(order));

        assertThrows(InvalidRequestException.class, () -> orderService.rejectOrder(user, orderId));
    }

}
