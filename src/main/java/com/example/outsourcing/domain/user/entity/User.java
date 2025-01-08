package com.example.outsourcing.domain.user.entity;

import com.example.outsourcing.domain.common.dto.AuthUser;
import com.example.outsourcing.domain.common.entity.Timestamped;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor
public class User extends Timestamped {

    public enum UserRole {OWNER, USER}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    public static User fromAuthUser(AuthUser authUser) {
        User user = new User();
        user.id = authUser.id();
        user.email = authUser.email();
        user.userRole = authUser.userRole();
        return user;
    }

    public User(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}
