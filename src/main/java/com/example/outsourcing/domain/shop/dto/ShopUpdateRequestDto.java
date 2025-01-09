package com.example.outsourcing.domain.shop.dto;

import jakarta.validation.constraints.Null;
import java.math.BigDecimal;
import java.time.LocalTime;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
public class ShopUpdateRequestDto {

    @Null(message = "수정하지 않을 경우 이름 생략 가능")
    private String name;

    @Null(message = "수정하지 않을 경우 최소 주문 금액 생략")
    private BigDecimal minOrderPrice;

    @Null(message = "수정하지 않을 경우 영업 시작 시간 생략")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime open;

    @Null(message = "수정하지 않은 경우 영업 종료 시간 생략")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime close;

    public ShopUpdateRequestDto(String name, BigDecimal minOrderPrice, LocalTime open,
        LocalTime close) {
        this.name = name;
        this.minOrderPrice = minOrderPrice;
        this.open = open;
        this.close = close;
    }

}
