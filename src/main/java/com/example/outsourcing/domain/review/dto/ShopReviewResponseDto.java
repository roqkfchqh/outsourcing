package com.example.outsourcing.domain.review.dto;

import java.time.LocalDateTime;

public record ShopReviewResponseDto(String username, String content, LocalDateTime time,
                                    int rating) {

}
