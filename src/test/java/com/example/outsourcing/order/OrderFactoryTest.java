package com.example.outsourcing.order;

import com.example.outsourcing.domain.order.service.OrderFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderFactoryTest {

    @InjectMocks
    private OrderFactory orderFactory;

//    @Test
//    void createOrder_ShouldCreateOrder_유효한_값() {
//        User user = new User();
//        ReflectionTestUtils.setField(user, "id", 1L);
//        ReflectionTestUtils.setField(user, "username", "Test User");
//
//        Shop shop = new Shop();
//        ReflectionTestUtils.setField(shop, "id", 1L);
//        ReflectionTestUtils.setField(shop, "name", "Test Shop");
//        ReflectionTestUtils.setField(shop, "minOrderPrice", BigDecimal.valueOf(50));
//
//        Menu menu = new Menu();
//        ReflectionTestUtils.setField(menu, "id", 1L);
//        ReflectionTestUtils.setField(menu, "name", "Test Menu");
//        ReflectionTestUtils.setField(menu, "price", BigDecimal.valueOf(10));
//        ReflectionTestUtils.setField(menu, "shop", shop);
//
//        Cart.MenuItem menuItem = new Cart.MenuItem(1L, 2);
//
//        Order order = orderFactory.createOrder(user, BigDecimal.valueOf(20), Map.of(1L, menu),
//            List.of(menuItem));
//
//        assertNotNull(order);
//        assertEquals(user, order.getUser());
//        assertEquals(1, order.getOrderMenus().size());
//        assertEquals(BigDecimal.valueOf(20), order.getTotalPrice());
//    }

//    @Test
//    void createOrder_ShouldHandleEmptyMenuItems_메뉴리스트_비어있을때() {
//        User user = new User();
//        ReflectionTestUtils.setField(user, "id", 1L);
//        ReflectionTestUtils.setField(user, "username", "Test User");
//
//        Order order = orderFactory.createOrder(user, BigDecimal.ZERO, Map.of(), List.of());
//
//        assertNotNull(order);
//        assertEquals(0, order.getOrderMenus().size());
//        assertEquals(BigDecimal.ZERO, order.getTotalPrice());
//    }
}
