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
public class OwnerCheckAspect {

    private final NativeWebRequest webRequest;

    public OwnerCheckAspect(NativeWebRequest webRequest) {
        this.webRequest = webRequest;
    }

    @Around("@annotation(ownerCheck)")
    public Object ownerCheck(ProceedingJoinPoint joinPoint, OwnerCheck ownerCheck) throws Throwable {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        UserRole userRole = (UserRole) request.getAttribute("userRole");

        if (userRole == null) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_OPERATION);
        }
        if(!userRole.equals(ownerCheck.value())){
            throw new ForbiddenException(ErrorCode.FORBIDDEN_OWNER);
        }

        return joinPoint.proceed();
    }

}
