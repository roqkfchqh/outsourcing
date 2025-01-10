package com.example.outsourcing.domain.order.service;

import com.example.outsourcing.domain.common.notification.WebSocketService;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.order.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private final OrderRepository orderRepository;
    private final WebSocketService webSocketService;

    // 1분에 한번 10분넘은 주문 자동거절
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expirePendingOrders() {
        List<Order> expiredOrders = orderRepository.findAllByStatusAndCreatedAtBefore(
            Order.Status.PENDING,
            LocalDateTime.now().minusMinutes(10)
        );

        //유저 id 추출
        List<Long> userIds = expiredOrders.stream()
            .map(order -> order.getUser().getId())
            .distinct() //중복 제거
            .toList();

        //알림
        String message = "주문이 자동 취소되었습니다.";
        webSocketService.sendNotificationToUser(userIds, message);

        orderRepository.deleteAll(expiredOrders);
    }

    // 10분에 한번 7일넘은 주문 리뷰작성권한 박탈
    @Scheduled(fixedRate = 600000)
    @Transactional
    public void expireReviewPeriods() {
        List<Order> expiredReviewOrders = orderRepository.findAllByStatusAndCreatedAtBefore(
            Order.Status.COMPLETED,
            LocalDateTime.now().minusDays(7)
        );

        expiredReviewOrders.forEach(Order::markCannotReview);
        orderRepository.saveAll(expiredReviewOrders);
    }
}
