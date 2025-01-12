package com.example.outsourcing.domain.review.service;

import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.common.exception.ForbiddenException;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.review.entity.Review;
import com.example.outsourcing.domain.review.repository.ReviewRepository;
import com.example.outsourcing.domain.shop.entity.Shop;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewValidator {

    private final ReviewRepository reviewRepository;

    public void canReview(Order order) {
        if (order.isCannotReview()) {
            throw new InvalidRequestException(ErrorCode.CANNOT_REVIEW);
        }
        if (order.getStatus() != Order.Status.COMPLETED) {
            throw new InvalidRequestException(ErrorCode.NOT_COMPLETED_ORDER);
        }
    }

    public void userIsOrderOwner(Order order, AuthUser user) {
        if (!Objects.equals(order.getUser().getId(), user.id())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_OPERATION);
        }
    }

    public void reviewAlreadyExist(Long orderId) {
        if (reviewRepository.existsByOrderId(orderId)) {
            throw new InvalidRequestException(ErrorCode.ALREADY_REVIEWED);
        }
    }

    public void shopIsActive(Shop shop) {
        if (shop.isDeleted()) {
            throw new InvalidRequestException(ErrorCode.SHOP_DELETED);
        }
    }

    public void userIsReviewOwner(AuthUser user, Review review) {
        if (!Objects.equals(review.getUser().getId(), user.id())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_OPERATION);
        }
    }
}

