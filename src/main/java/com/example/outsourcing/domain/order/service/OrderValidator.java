package com.example.outsourcing.domain.order.service;

import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.common.exception.ForbiddenException;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.order.entity.Order.Status;
import com.example.outsourcing.domain.order.repository.OrderRepository;
import com.example.outsourcing.domain.shop.entity.Shop;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderValidator {

    private final OrderRepository orderRepository;

    public Order userIsOrderShopOwner(AuthUser user, Long orderId) {
        return orderRepository.findOrderByOwner(orderId, user.id())
            .orElseThrow(() -> new ForbiddenException(ErrorCode.FORBIDDEN_OPERATION));
    }

    public void isAlreadyCompleted(Order order) {
        if (order.getStatus().equals(Status.COMPLETED)) {
            throw new InvalidRequestException(ErrorCode.ALREADY_COMPLETED);
        }
    }

    public void isPending(Order order) {
        if (order.getStatus() != Status.PENDING) {
            throw new InvalidRequestException(ErrorCode.CANNOT_CHANGE_STATUS);
        }
    }

    public void canGetOrder(AuthUser user, Long orderId, Order order) {
        if (!Objects.equals(order.getUser().getId(), user.id()) &&
            !orderRepository.existsOrderByOwner(orderId, user.id())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_OPERATION);
        }
    }

    public void shopIsActive(Shop shop) {
        if (shop.isDeleted()) {
            throw new InvalidRequestException(ErrorCode.SHOP_DELETED);
        }
    }

    public void userIsShopOwner(AuthUser authUser, Shop shop) {
        // 사장님이 해당 가게의 소유자인지 확인
        if (!shop.getUser().getId().equals(authUser.id())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_OPERATION);
        }
    }

}
