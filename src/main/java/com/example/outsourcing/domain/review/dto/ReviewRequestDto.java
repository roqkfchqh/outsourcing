package com.example.outsourcing.domain.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ReviewRequestDto(
    String content,
    @Min(value = 1, message = "별점은 1점에서 5점까지만 등록 가능합니다.")
    @Max(value = 5, message = "별점은 1점에서 5점까지만 등록 가능합니다.") int rating
) {

}
