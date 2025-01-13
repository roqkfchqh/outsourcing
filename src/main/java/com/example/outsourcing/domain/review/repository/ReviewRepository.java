package com.example.outsourcing.domain.review.repository;

import com.example.outsourcing.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

    boolean existsByOrderId(Long orderId);

    @EntityGraph(attributePaths = {"user", "shop", "order"})
    Page<Review> findAllByShopId(Long shopId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "shop", "order"})
    Page<Review> findAllByUserId(Long userId, Pageable pageable);
}
