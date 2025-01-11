package com.example.outsourcing.domain.review.repository;

import com.example.outsourcing.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {

    Page<Review> findShopReviews(Long shopId, int minRating, int maxRating, Pageable pageable);

    Page<Review> findUserReviews(Long userId, Pageable pageable);

}
