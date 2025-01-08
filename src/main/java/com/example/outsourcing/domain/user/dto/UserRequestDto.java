package com.example.outsourcing.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRequestDto {

  private String username;
  private String password;
  private String email;
}
