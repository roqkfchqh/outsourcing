package com.example.outsourcing.domain.user.service;

import static com.example.outsourcing.domain.common.exception.base.ErrorCode.ALREADY_USED_EMAIL;
import static com.example.outsourcing.domain.common.exception.base.ErrorCode.WRONG_EMAIL;
import static com.example.outsourcing.domain.common.exception.base.ErrorCode.WRONG_PASSWORD;

import com.example.outsourcing.domain.common.exception.AuthException;
import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.util.JwtUtil;
import com.example.outsourcing.domain.common.util.PasswordEncoder;
import com.example.outsourcing.domain.user.dto.UserRequestDto;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    public static final Set<String> expiredTokenSet = new HashSet<>(); // 별도의 클래스에서 관리할 것
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // set에 대한 개인공부를 추천

    @Transactional
    public void register(UserRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new InvalidRequestException(ALREADY_USED_EMAIL);
        }
    /*
    비밀번호 조건에 맞는지 확인하는 로직 추가해야함.
     */
        User user =
            new User(
                requestDto.getUsername(),
                passwordEncoder.encode(requestDto.getPassword()),
                requestDto.getEmail(),
                requestDto.getUserRole());

        User savedUser = userRepository.save(user);
    }

    public String login(UserRequestDto requestDto) {
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
        expiredTokenSet.add(token);
    }
}
