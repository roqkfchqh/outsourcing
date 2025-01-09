package com.example.outsourcing.domain.user.service;

import static com.example.outsourcing.domain.common.exception.base.ErrorCode.ALREADY_USED_EMAIL;
import static com.example.outsourcing.domain.common.exception.base.ErrorCode.WRONG_EMAIL;
import static com.example.outsourcing.domain.common.exception.base.ErrorCode.WRONG_PASSWORD;

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

    public void logout(String token) {
        JwtUtil.expiredTokenSet.add(token);
    }

}
