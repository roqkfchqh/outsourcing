package com.example.outsourcing.domain.common.notification;

import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.order.repository.OrderRepository;
import com.example.outsourcing.domain.review.dto.UserReviewResponseDto;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class NotificationAspect {

    private final WebSocketService webSocketService;
    private final OrderRepository orderRepository;
    private final ShopRepository shopRepository;

    @AfterReturning(
        pointcut = "execution(* com.example.outsourcing.domain.order.service.OrderService.toNextStatus(..)) && args(user, orderId)",
        returning = "order",
        argNames = "user,orderId,order")
    public void afterOrderStatusChanged(AuthUser user, Long orderId, Order order) {
        String message = getMessage(order);
        webSocketService.sendNotificationToUser(order.getUser().getId(), message);
    }

    @After(value = "execution(* com.example.outsourcing.domain.order.service.OrderService.rejectOrder(..)) && args(user, orderId)", argNames = "user,orderId")
    public void afterOrderRejected(AuthUser user, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        String message = "주문이 거절되었습니다. \n일시: " + order.getUpdatedAt().format(formatter);
        webSocketService.sendNotificationToUser(order.getUser().getId(), message);
    }

    @AfterReturning(
        pointcut = "execution(* com.example.outsourcing.domain.review.service.ReviewService.createReview(..))",
        returning = "review"
    )
    public void afterReviewCreated(UserReviewResponseDto review) {
        Shop shop = shopRepository.findById(review.shopId()).orElseThrow();
        Long ownerId = shop.getUser().getId();

        String message = String.format("새로운 리뷰가 작성되었습니다: %s (평점: %d)", review.content(),
            review.rating());

        webSocketService.sendNotificationToUser(ownerId, message);
    }

    private static String getMessage(Order order) {
        return switch (order.getStatus()) {
            case ACCEPT -> "주문이 수락되었습니다. \n일시: " + order.getUpdatedAt().format(formatter);
            case DELIVERING -> "라이더가 음식을 픽업했습니다. \n일시: " + order.getUpdatedAt().format(formatter);
            case COMPLETED -> "배달이 완료되었습니다. \n일시: " + order.getUpdatedAt().format(formatter);
            default -> "";
        };
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH시 mm분 ss초");
}
