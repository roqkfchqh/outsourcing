package com.example.outsourcing.domain.review.service;

import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.order.repository.OrderRepository;
import com.example.outsourcing.domain.review.dto.ReviewRequestDto;
import com.example.outsourcing.domain.review.dto.ShopReviewResponseDto;
import com.example.outsourcing.domain.review.dto.UserReviewResponseDto;
import com.example.outsourcing.domain.review.entity.Review;
import com.example.outsourcing.domain.review.mapper.ReviewMapper;
import com.example.outsourcing.domain.review.repository.ReviewRepository;
import com.example.outsourcing.domain.review.repository.ReviewRepositoryCustom;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import com.example.outsourcing.domain.user.entity.User;
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
    public final ReviewRepositoryCustom reviewRepositoryCustom;

    @Transactional
    public UserReviewResponseDto createReview(AuthUser user, Long orderId,
        ReviewRequestDto dto) {
        Order order = findOrder(orderId);
        order.validateIsCompleted();
        order.isCannotReview();
        order.validateOwnership(user);
        if (reviewRepository.existsByOrderId(orderId)) {
            throw new InvalidRequestException(ErrorCode.ALREADY_REVIEWED);
        }

        Shop shop = order.getShop();
        shop.validateIsActive();

        Review review = Review.of(User.fromAuthUser(user), shop, order, dto.content(),
            dto.rating());
        reviewRepository.save(review);
        return ReviewMapper.toUserReviewDto(review);
    }

    public Page<ShopReviewResponseDto> getShopReviews(Long shopId, int minRating, int maxRating,
        Pageable pageable) {
        Shop shop = findShop(shopId);
        shop.validateIsActive();
        Page<Review> reviews = reviewRepositoryCustom.findShopReviews(shopId, minRating, maxRating,
            pageable);
        return reviews.map(ReviewMapper::toShopReviewDto);
    }

    public Page<UserReviewResponseDto> getUserReviews(AuthUser user, Pageable pageable) {
        Page<Review> reviews = reviewRepositoryCustom.findUserReviews(user.id(), pageable);
        return reviews.map(ReviewMapper::toUserReviewDto);
    }

    @Transactional
    public void deleteReview(AuthUser user, Long reviewId) {
        Review review = findReview(reviewId);
        review.validateOwnership(user);
        reviewRepository.deleteById(reviewId);
    }

    /*
    helper
     */
    private Order findOrder(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.ORDER_NOT_FOUND));
    }

    private Review findReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.REVIEW_NOT_FOUND));
    }

    private Shop findShop(Long shopId) {
        return shopRepository.findById(shopId)
            .orElseThrow(() -> new InvalidRequestException(ErrorCode.SHOP_NOT_FOUND));
    }
}
