package com.example.outsourcing.domain.user.dto;

import com.example.outsourcing.domain.user.entity.User.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpRequestDto {

    @NotBlank
    private String username;
    @NotBlank
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,}")
    private String password;
    @Email
    @NotBlank
    private String email;
    private UserRole userRole;
}
