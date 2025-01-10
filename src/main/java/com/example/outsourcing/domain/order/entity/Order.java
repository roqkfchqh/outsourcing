package com.example.outsourcing.domain.order.entity;

import com.example.outsourcing.domain.common.entity.Timestamped;
import com.example.outsourcing.domain.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor
public class Order extends Timestamped {

    public enum Status {PENDING, ACCEPT, DELIVERING, COMPLETED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private Status status;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderMenu> orderMenus = new ArrayList<>();

    private BigDecimal totalPrice;

    public static Order of(User user, BigDecimal totalPrice) {
        Order order = new Order();
        order.user = user;
        order.status = Status.PENDING;
        order.totalPrice = totalPrice;
        return order;
    }

    public void addOrderMenu(OrderMenu orderMenu) {
        orderMenus.add(orderMenu);
        orderMenu.setOrder(this);
    }

    public void nextStatus() {
        switch (this.status) {
            case PENDING -> this.status = Status.ACCEPT;
            case ACCEPT -> this.status = Status.DELIVERING;
            case DELIVERING -> this.status = Status.COMPLETED;
            case COMPLETED -> throw new IllegalStateException("이미 완료된 주문입니다.");
            default -> throw new IllegalStateException("알 수 없는 상태입니다: " + this.status);
        }
    }

    public Order(BigDecimal totalPrice, User user, Status status, List<OrderMenu> orderMenus) {
        this.totalPrice = totalPrice;
        this.user = user;
        this.status = status;
        this.orderMenus = orderMenus;
    }

    public Order(User user, Status status, List<OrderMenu> orderMenus) {
        this.user = user;
        this.status = status;
        this.orderMenus = orderMenus;
    }

    public Order(User user, Status status) {
        this.user = user;
        this.status = status;
    }
}
