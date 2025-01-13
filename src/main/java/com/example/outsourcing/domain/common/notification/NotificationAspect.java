package com.example.outsourcing.domain.common.notification;

import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.order.dto.OrderResponseDto;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.review.dto.UserReviewResponseDto;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class NotificationAspect {

    private final WebSocketService webSocketService;
    private final ShopRepository shopRepository;
    private static final Logger logger = LoggerFactory.getLogger(NotificationAspect.class);

    @AfterReturning(
        pointcut = "execution(* com.example.outsourcing.domain.order.service.OrderService.createOrder(..)) && args(user)",
        returning = "order",
        argNames = "user,order")
    public void afterCreateOrder(AuthUser user, OrderResponseDto order) {
        String message =
            "주문~!\n" + "메뉴: " + order.orderMenu() + "\n총계: " + order.totalPrice().toString();
        webSocketService.sendNotificationToUser(order.ownerId(), message);

        logger.info("주문 생성 - 시간: {}, 가게 ID: {}, 주문 ID: {}",
            order.createdAt().format(formatter),
            order.shopId(),
            order.orderId()
        );
    }

    //주문 다음단계 알림(서버 -> 주문한손님)
    @AfterReturning(
        pointcut = "execution(* com.example.outsourcing.domain.order.service.OrderService.toNextStatus(..)) && args(user, orderId)",
        returning = "order",
        argNames = "user,orderId,order")
    public void afterOrderStatusChanged(AuthUser user, Long orderId, Order order) {
        String message = getMessage(order);
        webSocketService.sendNotificationToUser(order.getUser().getId(), message);

        logger.info("주문 상태 변경 - 시간: {}, 주문 ID: {}, 상태: {}",
            order.getUpdatedAt().format(formatter),
            order.getId(),
            order.getStatus()
        );
    }

    //주문 거절 알림(서버 -> 주문한손님)
    @AfterReturning(
        pointcut = "execution(* com.example.outsourcing.domain.order.service.OrderService.rejectOrder(..)) && args(user, orderId)",
        returning = "userId",
        argNames = "user,orderId,userId")
    public void afterOrderRejected(AuthUser user, Long orderId, Long userId) {
        String message = "주문이 거절되었습니다.";
        webSocketService.sendNotificationToUser(userId, message);

        logger.info("주문 거절 - 시간: {}, 주문 ID: {}, 사용자 ID: {}",
            formatter.format(java.time.LocalDateTime.now()),
            orderId,
            userId
        );
    }

    //리뷰 작성 알림(서버 -> 상점 주인)
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
