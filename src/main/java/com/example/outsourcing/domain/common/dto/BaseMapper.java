package com.example.outsourcing.domain.common.dto;

public class BaseMapper {

    public static <T> BaseResponseDto<T> map(T data) {
        return new BaseResponseDto<>(data);
    }
}
