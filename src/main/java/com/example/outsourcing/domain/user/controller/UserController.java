package com.example.outsourcing.domain.user.controller;

import com.example.outsourcing.domain.user.dto.UserRequestDto;
import com.example.outsourcing.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<String> userResgister(@Valid @RequestBody UserRequestDto requestDto) {
    userService.register(requestDto);
    return ResponseEntity.ok("회원가입에 성공했습니다.");
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@Valid @RequestBody UserRequestDto requestDto) {
    userService.login(requestDto);
    return ResponseEntity.ok("로그인에 성공했습니다.");
  }
}
