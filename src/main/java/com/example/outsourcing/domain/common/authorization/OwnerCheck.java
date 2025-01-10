package com.example.outsourcing.domain.common.authorization;

import com.example.outsourcing.domain.user.entity.User.UserRole;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface OwnerCheck {
    UserRole value() default UserRole.OWNER;

}
