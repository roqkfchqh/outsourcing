package com.example.outsourcing.domain.review.dto;

import java.time.LocalDateTime;

public record UserReviewResponseDto(String shopName, String content, LocalDateTime time,
                                    int rating) {

}
