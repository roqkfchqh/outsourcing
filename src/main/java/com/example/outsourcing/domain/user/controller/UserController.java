package com.example.outsourcing.domain.user.controller;

import com.example.outsourcing.domain.common.dto.BaseMapper;
import com.example.outsourcing.domain.common.dto.BaseResponseDto;
import com.example.outsourcing.domain.common.dto.MessageResponseDto;
import com.example.outsourcing.domain.user.dto.LoginRequestDto;
import com.example.outsourcing.domain.user.dto.SignUpRequestDto;
import com.example.outsourcing.domain.user.dto.UserResponseDto;
import com.example.outsourcing.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponseDto<MessageResponseDto>> register(
        @Valid @RequestBody SignUpRequestDto requestDto) {
        userService.register(requestDto);
        MessageResponseDto data = new MessageResponseDto(
            requestDto.getEmail() + " 님 회원가입에 성공했습니다.");
        return ResponseEntity.ok(BaseMapper.map(data));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponseDto<UserResponseDto>> login(
        @Valid @RequestBody LoginRequestDto requestDto) {
        String token = userService.login(requestDto);
        MessageResponseDto data = new MessageResponseDto(requestDto.getEmail() + " 님 로그인에 성공했습니다.");
        return ResponseEntity.ok(BaseMapper.map(new UserResponseDto(token, data)));
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponseDto<MessageResponseDto>> logout(
        @RequestHeader("Authorization") String token) {
        userService.logout(token);
        MessageResponseDto data = new MessageResponseDto("로그아웃에 성공했습니다.");
        return ResponseEntity.ok(BaseMapper.map(data));
    }
}
