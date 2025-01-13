package com.example.outsourcing.domain.order.controller;

import com.example.outsourcing.domain.common.annotation.Auth;
import com.example.outsourcing.domain.common.authorization.OwnerCheck;
import com.example.outsourcing.domain.common.authorization.UserCheck;
import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.common.dto.BaseMapper;
import com.example.outsourcing.domain.common.dto.BaseResponseDto;
import com.example.outsourcing.domain.common.dto.MessageResponseDto;
import com.example.outsourcing.domain.order.dto.OrderResponseDto;
import com.example.outsourcing.domain.order.service.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 요청 (수락/거절 가능한 상태로 변경, 장바구니 캐시 삭제)
     */
    @UserCheck
    @PostMapping
    public ResponseEntity<BaseResponseDto<OrderResponseDto>> createOrder(
        @Auth AuthUser authUser
    ) {
        OrderResponseDto order = orderService.createOrder(authUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseMapper.map(order));
    }

    /**
     * 주문 상태 "다음 상태"로 변경 (보류 -> 수락 -> 배달 중 -> 배달 완료)
     */
    @OwnerCheck
    @PatchMapping("/{orderId}")
    public ResponseEntity<BaseResponseDto<MessageResponseDto>> toNextStatus(
        @Auth AuthUser authUser,
        @PathVariable Long orderId
    ) {
        orderService.toNextStatus(authUser, orderId);
        MessageResponseDto data = new MessageResponseDto(orderId + " 번 주문이 다음 상태로 변경되었습니다.");
        return ResponseEntity.ok(BaseMapper.map(data));
    }

    /**
     * 주문 상태 조회
     */
    @UserCheck
    @GetMapping("/{orderId}")
    public ResponseEntity<BaseResponseDto<OrderResponseDto>> getOrder(
        @Auth AuthUser authUser,
        @PathVariable Long orderId
    ) {
        OrderResponseDto order = orderService.getOrder(authUser, orderId);
        return ResponseEntity.ok(BaseMapper.map(order));
    }

    /**
     * 주문 거절
     */
    @OwnerCheck
    @DeleteMapping("/{orderId}")
    public ResponseEntity<BaseResponseDto<MessageResponseDto>> rejectOrder(
        @Auth AuthUser authUser,
        @PathVariable Long orderId
    ) {
        orderService.rejectOrder(authUser, orderId);
        MessageResponseDto data = new MessageResponseDto(orderId + " 번 주문을 거절하였습니다.");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(BaseMapper.map(data));
    }

    /**
     * 사장님이 가게의 모든 주문 요청 조회
     */
    @OwnerCheck
    @GetMapping
    public ResponseEntity<BaseResponseDto<List<OrderResponseDto>>> getOrdersByShop(
        @Auth AuthUser authUser,
        @RequestParam Long shopId
    ) {
        List<OrderResponseDto> orders = orderService.getOrdersByShop(authUser, shopId);
        return ResponseEntity.ok(BaseMapper.map(orders));
    }
}
