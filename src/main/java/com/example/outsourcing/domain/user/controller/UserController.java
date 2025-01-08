package com.example.outsourcing.domain.user.controller;

import com.example.outsourcing.domain.user.dto.UserRequestDto;
import com.example.outsourcing.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/auth/register")
  public ResponseEntity<String> userResgister(@Valid @RequestBody UserRequestDto requestDto) {
    userService.register(requestDto);
    return ResponseEntity.ok("회원가입에 성공했습니다.");
  }
}
