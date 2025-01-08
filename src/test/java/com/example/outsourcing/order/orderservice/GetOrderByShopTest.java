package com.example.outsourcing.order.orderservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.outsourcing.domain.cart.entity.OrderMenu;
import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.common.exception.ForbiddenException;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.order.dto.OrderResponseDto;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.order.repository.OrderRepository;
import com.example.outsourcing.domain.order.service.OrderService;
import com.example.outsourcing.domain.shop.entity.Menu;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
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
        Shop shop = new Shop(1L, User.fromAuthUser(user), "Test Shop", BigDecimal.valueOf(50),
            LocalTime.parse("09:00:00"),
            LocalTime.parse("18:00:00"), false);
        Menu menu = new Menu(1L, "Test Menu", BigDecimal.TEN, shop);
        OrderMenu orderMenu = OrderMenu.of(menu, 2);
        Order order = new Order(User.fromAuthUser(user), Order.Status.PENDING, List.of(orderMenu));
        orderMenu.assignOrder(order);

        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        when(orderRepository.findAllByShopId(shopId)).thenReturn(List.of(order));

        List<OrderResponseDto> responses = orderService.getOrdersByShop(user, shopId);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Test Shop", responses.get(0).shopName());
    }

    @Test
    void getOrdersByShop_ShouldThrowInvalidRequestException_폐업한_가게일때() {
        AuthUser user = new AuthUser(1L, "testOwner", UserRole.OWNER);
        Long shopId = 1L;
        Shop shop = new Shop(1L, User.fromAuthUser(user), "Test Shop", BigDecimal.valueOf(50),
            LocalTime.parse("09:00:00"),
            LocalTime.parse("18:00:00"), true);

        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));

        assertThrows(InvalidRequestException.class,
            () -> orderService.getOrdersByShop(user, shopId));
    }

    @Test
    void getOrdersByShop_ShouldThrowForbiddenException_오너가_아닐때() {
        AuthUser user = new AuthUser(2L, "notOwner", UserRole.USER);
        Long shopId = 1L;
        Shop shop = new Shop(shopId, new User(1L, "realOwner"), "Test Shop",
            BigDecimal.valueOf(50), LocalTime.parse("09:00:00"), LocalTime.parse("18:00:00"),
            false);

        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));

        assertThrows(ForbiddenException.class,
            () -> orderService.getOrdersByShop(user, shopId));
    }


}
