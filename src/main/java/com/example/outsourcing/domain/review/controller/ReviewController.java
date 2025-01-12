package com.example.outsourcing.domain.review.controller;

import com.example.outsourcing.domain.common.annotation.Auth;
import com.example.outsourcing.domain.common.authorization.UserCheck;
import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.common.dto.BaseMapper;
import com.example.outsourcing.domain.common.dto.BaseResponseDto;
import com.example.outsourcing.domain.common.dto.MessageResponseDto;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;
import com.example.outsourcing.domain.review.dto.ReviewRequestDto;
import com.example.outsourcing.domain.review.dto.ShopReviewResponseDto;
import com.example.outsourcing.domain.review.dto.UserReviewResponseDto;
import com.example.outsourcing.domain.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shops/orders/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private static final String PAGE_COUNT = "1";
    private static final String PAGE_SIZE = "10";

    private final ReviewService reviewService;

    @UserCheck
    @PostMapping
    public ResponseEntity<BaseResponseDto<UserReviewResponseDto>> createReview(
        @Auth AuthUser authUser,
        @RequestParam Long orderId,
        @RequestBody @Valid ReviewRequestDto dto
    ) {
        UserReviewResponseDto review = reviewService.createReview(authUser, orderId, dto);
        return ResponseEntity.ok(BaseMapper.map(review));
    }

    @GetMapping
    public ResponseEntity<BaseResponseDto<Page<?>>> getReviews(
        @RequestParam(required = false) Long shopId,
        @RequestParam(required = false) Integer minRating,
        @RequestParam(required = false) Integer maxRating,
        @Auth AuthUser authUser,
        @RequestParam(defaultValue = PAGE_COUNT) int page,
        @RequestParam(defaultValue = PAGE_SIZE) int size
    ) {
        Pageable pageable = validatePageSize(page, size);

        if (shopId != null) {
            int min = (minRating != null) ? minRating : 0;
            int max = (maxRating != null) ? maxRating : 5;
            Page<ShopReviewResponseDto> reviews = reviewService.getShopReviews(shopId, min, max,
                pageable);
            return ResponseEntity.ok(BaseMapper.map(reviews));
        }
        if (authUser.id() != null) {
            Page<UserReviewResponseDto> reviews = reviewService.getUserReviews(authUser, pageable);
            return ResponseEntity.ok(BaseMapper.map(reviews));
        }
        return ResponseEntity.notFound().build();
    }

    @UserCheck
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<BaseResponseDto<MessageResponseDto>> deleteReview(
        @Auth AuthUser authUser,
        @PathVariable Long reviewId
    ) {
        reviewService.deleteReview(authUser, reviewId);
        MessageResponseDto data = new MessageResponseDto(reviewId + " 번 리뷰가 삭제되었습니다.");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(BaseMapper.map(data));
    }

    private Pageable validatePageSize(int page, int size) {
        if (page < 1 || size < 1) {
            throw new InvalidRequestException(ErrorCode.PAGING_ERROR);
        }
        return PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
    }

}
