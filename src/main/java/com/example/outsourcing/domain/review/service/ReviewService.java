package com.example.outsourcing.domain.review.service;

import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.common.exception.ForbiddenException;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.order.entity.Order.Status;
import com.example.outsourcing.domain.order.repository.OrderRepository;
import com.example.outsourcing.domain.review.dto.ReviewRequestDto;
import com.example.outsourcing.domain.review.dto.ShopReviewResponseDto;
import com.example.outsourcing.domain.review.dto.UserReviewResponseDto;
import com.example.outsourcing.domain.review.entity.Review;
import com.example.outsourcing.domain.review.mapper.ReviewMapper;
import com.example.outsourcing.domain.review.repository.ReviewRepository;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import com.example.outsourcing.domain.user.entity.User;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    public final ReviewRepository reviewRepository;
    public final OrderRepository orderRepository;
    public final ShopRepository shopRepository;

    @Transactional
    public UserReviewResponseDto createReview(AuthUser user, Long orderId,
        ReviewRequestDto dto) {
        Order order = findOrder(orderId);
        //리뷰 가능 여부 확인
        if (order.isCannotReview()) {
            throw new InvalidRequestException(ErrorCode.CANNOT_REVIEW);
        }
        // 주문 완료여부 확인
        if (order.getStatus() != Status.COMPLETED) {
            throw new InvalidRequestException(ErrorCode.NOT_COMPLETED_ORDER);
        }
        // 주문자 정보와 동일한지 확인
        if (!Objects.equals(order.getUser().getId(), user.id())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_OPERATION);
        }
        // 해당 주문에 대한 리뷰가 이미 존재하는지 확인
        if (reviewRepository.existsByOrderId(orderId)) {
            throw new InvalidRequestException(ErrorCode.ALREADY_REVIEWED);
        }

        Shop shop = order.getOrderMenus().stream()
            .findFirst()
            .map(orderMenu -> orderMenu.getMenu().getShop())
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.SHOP_NOT_FOUND));
        if (shop.isDeleted()) {
            throw new InvalidRequestException(ErrorCode.SHOP_DELETED);
        }

        Review review = Review.of(User.fromAuthUser(user), shop, order, dto.content(),
            dto.rating());
        reviewRepository.save(review);
        return ReviewMapper.toUserReviewDto(review);
    }

    public Page<ShopReviewResponseDto> getShopReviews(Long shopId, Pageable pageable) {
        Shop shop = shopRepository.findById(shopId)
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.SHOP_NOT_FOUND));
        if (shop.isDeleted()) {
            throw new ForbiddenException(ErrorCode.SHOP_DELETED);
        }
        Page<Review> reviews = reviewRepository.findAllByShopId(shopId, pageable);
        return reviews.map(ReviewMapper::toShopReviewDto);
    }

    public Page<UserReviewResponseDto> getUserReviews(AuthUser user, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findAllByUserId(user.id(), pageable);
        return reviews.map(ReviewMapper::toUserReviewDto);
    }

    @Transactional
    public void deleteReview(AuthUser user, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.REVIEW_NOT_FOUND));
        if (!Objects.equals(review.getUser().getId(), user.id())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_OPERATION);
        }
        reviewRepository.deleteById(reviewId);
    }

    /*
    helper
     */
    private Order findOrder(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.ORDER_NOT_FOUND));
    }
}
