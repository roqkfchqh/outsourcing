package com.example.outsourcing.domain.review.service;

import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;
import com.example.outsourcing.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewValidator {

    private final ReviewRepository reviewRepository;

    public void reviewAlreadyExist(Long orderId) {
        if (reviewRepository.existsByOrderId(orderId)) {
            throw new InvalidRequestException(ErrorCode.ALREADY_REVIEWED);
        }
    }
}
