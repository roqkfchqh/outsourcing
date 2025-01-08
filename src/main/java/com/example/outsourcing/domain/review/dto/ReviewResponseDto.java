package com.example.outsourcing.domain.review.dto;

import java.time.LocalDateTime;

public record ReviewResponseDto(String username, String content, LocalDateTime time, int rating) {

}
