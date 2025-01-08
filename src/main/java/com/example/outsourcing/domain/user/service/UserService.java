package com.example.outsourcing.domain.user.service;

import static com.example.outsourcing.domain.common.exception.base.ErrorCode.ALREADY_USED_EMAIL;

import com.example.outsourcing.domain.common.exception.InvalidRequestException;
import com.example.outsourcing.domain.common.util.JwtUtil;
import com.example.outsourcing.domain.common.util.PasswordEncoder;
import com.example.outsourcing.domain.user.dto.UserRequestDto;
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

  // public UserResponseDto login(UserRequestDto requestDto) {}

  //  public UserResponseDto logout(UserRequestDto requestDto) {
  //
  //  }
}
