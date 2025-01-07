package com.example.outsourcing.domain.cart.entity;

import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.shop.entity.Menu;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "order_menus")
@NoArgsConstructor
public class OrderMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    //@Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int quantity;
    private BigDecimal price = BigDecimal.ZERO;

    public static OrderMenu of(Menu menu, int quantity, BigDecimal price) {
        OrderMenu orderMenu = new OrderMenu();
        orderMenu.menu = menu;
        orderMenu.quantity = quantity;
        orderMenu.price = price;
        return orderMenu;
    }

}
