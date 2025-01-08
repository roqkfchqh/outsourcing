package com.example.outsourcing.domain.review.mapper;

import com.example.outsourcing.domain.review.dto.ReviewResponseDto;
import com.example.outsourcing.domain.review.dto.UserReviewResponseDto;
import com.example.outsourcing.domain.review.entity.Review;

public class ReviewMapper {

    public static ReviewResponseDto toShopReviewDto(Review review) {
        return new ReviewResponseDto(
            review.getUser().getUsername(),
            review.getContent(),
            review.getCreatedAt(),
            review.getRating()
        );
    }

    public static UserReviewResponseDto toUserReviewDto(Review review) {
        return new UserReviewResponseDto(
            review.getShop().getName(),
            review.getContent(),
            review.getCreatedAt(),
            review.getRating()
        );
    }
}
