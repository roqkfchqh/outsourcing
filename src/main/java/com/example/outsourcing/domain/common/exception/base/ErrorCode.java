package com.example.outsourcing.domain.common.exception.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    //user
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 존재하지 않습니다. 로그인이 필요합니다."),
    WRONG_EMAIL(HttpStatus.UNAUTHORIZED, "이메일을 잘못 입력하였습니다."),
    WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호를 잘못 입력하였습니다."),
    AUTH(HttpStatus.UNAUTHORIZED, "@Auth와 AuthUser 타입은 함께 사용되어야 합니다."),

    //invalid
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 사용자입니다."),
    ALREADY_USED_EMAIL(HttpStatus.BAD_REQUEST, "이미 사용중인 이메일입니다."),
    PAGING_ERROR(HttpStatus.BAD_REQUEST, "페이지 입력값이 잘못되었습니다."),
    ORDER_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 주문이 존재하지 않습니다."),
    CANNOT_CHANGE_STATUS(HttpStatus.BAD_REQUEST, "주문 상태를 바꿀 수 없습니다."),
    CART_IS_EMPTY(HttpStatus.BAD_REQUEST, "장바구니가 비어있습니다."),
    CART_ITEM_NOT_FOUND(HttpStatus.BAD_REQUEST, "카트에서 해당 메뉴를 찾을 수 없습니다."),
    MENU_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 메뉴가 존재하지 않습니다."),
    MINIMUM_ORDER_NOT_MET(HttpStatus.BAD_REQUEST, "주문 가격이 최소주문금액보다 작습니다."),
    SHOP_CLOSED(HttpStatus.BAD_REQUEST, "영업시간이 아닙니다."),
    SHOP_DELETED(HttpStatus.BAD_REQUEST, "가게가 망했습니다."),
    SHOP_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 가게를 찾을 수 없습니다."),
    ALREADY_REVIEWED(HttpStatus.BAD_REQUEST, "해당 주문에 대한 리뷰가 이미 존재합니다."),
    NOT_COMPLETED_ORDER(HttpStatus.BAD_REQUEST, "완료 처리된 주문이 아닙니다."),
    MENU_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "같은 이름의 메뉴가 이미 존재합니다."),
    SHOP_HOURS_INVALID(HttpStatus.BAD_REQUEST, "영업 시작 시간이 영업 종료 시간 이후일 수 없습니다."),
    REVIEW_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 리뷰를 찾을 수 없습니다."),
    MIN_ORDER_PRICE_MUST_BE_GREATER_THAN_ZERO(HttpStatus.BAD_REQUEST, "최소 주문 금액은 0보다 커야 합니다."),
    PRICE_MUST_BE_GREATER_THAN_ZERO(HttpStatus.BAD_REQUEST, "가격은 0보다 커야 합니다."),
    USER_MAX_SHOPS_REACHED(HttpStatus.BAD_REQUEST, "최대 허용된 가게 수를 초과했습니다."),

    //forbidden
    FORBIDDEN_OPERATION(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    FORBIDDEN_OWNER(HttpStatus.FORBIDDEN, "권한이 없습니다. 사장님 계정만 접근이 가능합니다."),
    FORBIDDEN_USER(HttpStatus.FORBIDDEN, "권한이 없습니다. 손님 계정만 접근이 가능합니다.."),

    //server
    CACHE_CONFIGURATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "캐시 설정 오류입니다. 캐시 구성을 확인하세요.");


    private final HttpStatus status;
    private final String message;
}
