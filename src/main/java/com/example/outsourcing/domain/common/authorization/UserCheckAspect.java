package com.example.outsourcing.domain.common.authorization;

import com.example.outsourcing.domain.common.exception.ForbiddenException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;
import com.example.outsourcing.domain.user.entity.User.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

@Aspect
@Component
public class UserCheckAspect {

    private final NativeWebRequest webRequest;

    public UserCheckAspect(NativeWebRequest webRequest) {
        this.webRequest = webRequest;
    }

    @Around("@annotation(userCheck)")
    public Object userCheck(ProceedingJoinPoint joinPoint, UserCheck userCheck) throws Throwable {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        UserRole userRole = (UserRole) request.getAttribute("userRole");

        if (userRole == null) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_OPERATION);
        }
        if(!userRole.equals(userCheck.value())){
            throw new ForbiddenException(ErrorCode.FORBIDDEN_USER);
        }

        return joinPoint.proceed();
    }
}

