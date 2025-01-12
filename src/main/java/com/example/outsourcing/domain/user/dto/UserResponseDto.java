package com.example.outsourcing.domain.user.dto;

import com.example.outsourcing.domain.common.dto.MessageResponseDto;

public record UserResponseDto(String bearerToken, MessageResponseDto message) {

}
