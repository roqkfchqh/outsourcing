package com.example.outsourcing.domain.common.authorization;

import com.example.outsourcing.domain.common.exception.ForbiddenException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;
import com.example.outsourcing.domain.user.entity.User.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserCheckAspect {

    private final HttpServletRequest httpRequest;

    public UserCheckAspect(HttpServletRequest httprequest) {
        this.httpRequest = httprequest;
    }

    @Around("@annotation(userCheck)")
    public Object userCheck(ProceedingJoinPoint joinPoint, UserCheck userCheck) throws Throwable {
        UserRole userRole = (UserRole) httpRequest.getAttribute("userRole");

        if (userRole == null) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_ROLE);
        }

        if (!userRole.equals(userCheck.value())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_OPERATION);
        }

        return joinPoint.proceed();
    }
}
