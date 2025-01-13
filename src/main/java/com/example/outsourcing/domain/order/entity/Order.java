package com.example.outsourcing.domain.order.entity;

import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.common.entity.Timestamped;
import com.example.outsourcing.domain.common.exception.ForbiddenException;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;
import com.example.outsourcing.domain.shop.entity.Shop;
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
import java.util.Objects;
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

    private boolean cannotReview = false;

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

    public void markCannotReview() {
        // 리뷰 상태 관리 필드 업데이트
        this.cannotReview = true;
    }

    public void isCannotReview() {
        if (cannotReview) {
            throw new InvalidRequestException(ErrorCode.CANNOT_REVIEW);
        }
    }

    public void validateIsPending() {
        if (status != Status.PENDING) {
            throw new InvalidRequestException(ErrorCode.CANNOT_CHANGE_STATUS);
        }
    }

    public void validateIsNotCompleted() {
        if (status == Status.COMPLETED) {
            throw new InvalidRequestException(ErrorCode.ALREADY_COMPLETED);
        }
    }

    public void validateIsCompleted() {
        if (status != Status.COMPLETED) {
            throw new InvalidRequestException(ErrorCode.NOT_COMPLETED_ORDER);
        }
    }

    public Shop getShop() {
        return orderMenus.stream()
            .findFirst()
            .map(orderMenu -> orderMenu.getMenu().getShop())
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.SHOP_NOT_FOUND));
    }

    public void validateOwnership(AuthUser authUser) {
        if (!Objects.equals(user.getId(), authUser.id())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_OPERATION);
        }
    }
}
