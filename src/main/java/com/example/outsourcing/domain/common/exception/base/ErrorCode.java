package com.example.outsourcing.domain.common.exception.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  // user
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 존재하지 않습니다. 로그인이 필요합니다."),
  WRONG_EMAIL_OR_PASSWORD(HttpStatus.UNAUTHORIZED, "이메일이나 비밀번호를 잘못 입력하였습니다."),
  WRONG_EMAIL(HttpStatus.UNAUTHORIZED, "이메일을 잘못 입력하였습니다."),
  AUTH(HttpStatus.UNAUTHORIZED, "@Auth와 AuthUser 타입은 함께 사용되어야 합니다."),

  // invalid
  USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 사용자입니다."),
  ALREADY_USED_EMAIL(HttpStatus.BAD_REQUEST, "이미 사용중인 이메일입니다."),
  WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호를 잘못 입력하였습니다."),
  PAGING_ERROR(HttpStatus.BAD_REQUEST, "페이지 입력값이 잘못되었습니다."),
  ID_NULL(HttpStatus.BAD_REQUEST, "아이디 입력값이 잘못되었습니다."),
  TODO_USER_NOT_VALID(HttpStatus.BAD_REQUEST, "일정 작성자가 아닙니다."),
  USER_MANAGER_CANNOT(HttpStatus.BAD_REQUEST, "일정 작성자는 본인을 담당자로 등록할 수 없습니다."),
  COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 댓글이 존재하지 않습니다."),
  TODO_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 게시글이 존재하지 않습니다."),
  MANAGER_NOT_FOUND(HttpStatus.BAD_REQUEST, "매니저가 존재하지 않습니다."),
  TODO_MANAGER_NOT_VALID(HttpStatus.BAD_REQUEST, "해당 일정에 등록된 담당자가 아닙니다."),

  // forbidden
  FORBIDDEN_OPERATION(HttpStatus.FORBIDDEN, "권한이 없습니다."),

  // server
  CACHING_WRAPPER_ERROR(
      HttpStatus.INTERNAL_SERVER_ERROR, "ContentCachingWrapper 가 설정되지 않았습니다. 필터를 확인하세요."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류입니다.");

  private final HttpStatus status;
  private final String message;
}
