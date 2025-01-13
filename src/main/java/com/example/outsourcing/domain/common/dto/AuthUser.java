package com.example.outsourcing.domain.common.dto;

import com.example.outsourcing.domain.user.entity.User.UserRole;

public record AuthUser(Long id, String email, UserRole userRole) {

}
