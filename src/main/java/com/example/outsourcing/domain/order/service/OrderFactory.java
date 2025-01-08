package com.example.outsourcing.domain.order.service;

import com.example.outsourcing.domain.cart.entity.Cart;
import com.example.outsourcing.domain.cart.entity.Cart.MenuItem;
import com.example.outsourcing.domain.cart.entity.OrderMenu;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.shop.entity.Menu;
import com.example.outsourcing.domain.user.entity.User;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class OrderFactory {

    public Order createOrder(
        User user,
        Map<Long, Menu> menus,
        List<MenuItem> items
    ) {
        Order order = Order.of(user);
        for (Cart.MenuItem item : items) {
            Menu menu = menus.get(item.getMenuId());
            OrderMenu orderMenu = OrderMenu.of(menu, item.getQuantity());
            order.addOrderMenu(orderMenu);
        }
        return order;
    }

}
