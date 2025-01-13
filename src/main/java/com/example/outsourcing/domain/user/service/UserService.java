package com.example.outsourcing.domain.user.service;

import static com.example.outsourcing.domain.common.exception.base.ErrorCode.ALREADY_USED_EMAIL;
import static com.example.outsourcing.domain.common.exception.base.ErrorCode.INVALID_TOKEN;
import static com.example.outsourcing.domain.common.exception.base.ErrorCode.WRONG_EMAIL;
import static com.example.outsourcing.domain.common.exception.base.ErrorCode.WRONG_PASSWORD;
import static com.example.outsourcing.domain.common.util.JwtUtil.expiredTokenSet;

import com.example.outsourcing.domain.common.exception.AuthException;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.util.JwtUtil;
import com.example.outsourcing.domain.common.util.PasswordEncoder;
import com.example.outsourcing.domain.user.dto.LoginRequestDto;
import com.example.outsourcing.domain.user.dto.SignUpRequestDto;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public void register(SignUpRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new InvalidRequestException(ALREADY_USED_EMAIL);
        }
        User user =
            new User(
                requestDto.getUsername(),
                passwordEncoder.encode(requestDto.getPassword()),
                requestDto.getEmail(),
                requestDto.getUserRole());

        userRepository.save(user);
    }

    public String login(LoginRequestDto requestDto) {
        User user =
            userRepository
                .findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new AuthException(WRONG_EMAIL));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new AuthException(WRONG_PASSWORD);
        }
        return jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole());
    }

    //jwt 필터에서는 회원가입, 로그아웃, 로그인의 필터링이 없어야 httpRequest.setAttribute로 토큰값이 저장되는 구조라서 로그아웃 여부에 대해서는 별도로 여기서 예외처리
    public void logout(String token) {
        if (expiredTokenSet.contains(token)) {
            throw new AuthException(INVALID_TOKEN);
        }
        expiredTokenSet.add(token);
    }

}
