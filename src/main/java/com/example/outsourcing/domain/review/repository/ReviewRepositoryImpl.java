package com.example.outsourcing.domain.review.repository;

import com.example.outsourcing.domain.review.entity.QReview;
import com.example.outsourcing.domain.review.entity.Review;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Review> findShopReviews(Long shopId, int minRating, int maxRating,
        Pageable pageable) {
        QReview review = QReview.review;

        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(review.shop.id.eq(shopId));
        whereClause.and(review.rating.between(minRating, maxRating));

        List<Review> reviews = queryFactory.selectFrom(review)
            .where(whereClause)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(review.createdAt.desc())
            .fetch();

        Long count = queryFactory.select(review.count())
            .from(review)
            .where(
                review.shop.id.eq(shopId),
                review.rating.between(minRating, maxRating)
            )
            .fetchOne();

        long total = (count != null) ? count : 0;

        return new PageImpl<>(reviews, pageable, total);
    }

    @Override
    public Page<Review> findUserReviews(Long userId, Pageable pageable) {
        QReview review = QReview.review;

        List<Review> reviews = queryFactory.selectFrom(review)
            .where(review.user.id.eq(userId))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(review.createdAt.desc())
            .fetch();

        Long count = queryFactory.select(review.id.count())
            .from(review)
            .where(review.user.id.eq(userId))
            .fetchOne();

        long total = (count != null) ? count : 0;

        return new PageImpl<>(reviews, pageable, total);
    }
}
