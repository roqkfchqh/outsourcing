package com.example.outsourcing.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.outsourcing.domain.cart.entity.Cart;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.order.service.OrderFactory;
import com.example.outsourcing.domain.shop.entity.Menu;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.user.entity.User;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderFactoryTest {

    @InjectMocks
    private OrderFactory orderFactory;

    @Test
    void createOrder_ShouldCreateOrder_WithValidInputs() {
        User user = new User(1L, "Test User");
        Menu menu = new Menu(1L, "Test Menu", BigDecimal.valueOf(10),
            new Shop(1L, "Test Shop", BigDecimal.valueOf(50), null, null, false));
        Cart.MenuItem menuItem = new Cart.MenuItem(1L, 2);

        Order order = orderFactory.createOrder(user, Map.of(1L, menu), List.of(menuItem));

        assertNotNull(order);
        assertEquals(user, order.getUser());
        assertEquals(1, order.getOrderMenus().size());
    }
}
