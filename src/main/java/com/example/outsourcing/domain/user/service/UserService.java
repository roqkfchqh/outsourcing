package com.example.outsourcing.domain.user.service;

import static com.example.outsourcing.domain.common.exception.base.ErrorCode.ALREADY_USED_EMAIL;

import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.util.JwtUtil;
import com.example.outsourcing.domain.common.util.PasswordEncoder;
import com.example.outsourcing.domain.user.dto.UserRequestDto;
import com.example.outsourcing.domain.user.dto.UserResponseDto;
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
  public UserResponseDto register(UserRequestDto requestDto) {
    if (userRepository.existsByEmail(requestDto.getEmail())) {
      throw new InvalidRequestException(ALREADY_USED_EMAIL);
    }
    User user =
        new User(
            requestDto.getUsername(),
            passwordEncoder.encode(requestDto.getPassword()),
            requestDto.getEmail());

    User savedUser = userRepository.save(user);
    String bearerToken =
        jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), savedUser.getUserRole());
    return new UserResponseDto(bearerToken);
  }
}
