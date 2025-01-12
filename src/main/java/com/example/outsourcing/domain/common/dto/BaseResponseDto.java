package com.example.outsourcing.domain.common.dto;

import java.time.LocalDateTime;

public record BaseResponseDto<T>(LocalDateTime timestamp, T data) {

    public BaseResponseDto(T data) {
        this(LocalDateTime.now(), data);
    }
}