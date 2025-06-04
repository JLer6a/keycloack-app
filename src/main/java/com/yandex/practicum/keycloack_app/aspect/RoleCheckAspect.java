package com.yandex.practicum.keycloack_app.aspect;

import com.yandex.practicum.keycloack_app.exceptionhandler.ManagerAccessException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RoleCheckAspect {

    @Before("@annotation(com.yandex.practicum.keycloack_app.annotation.ManagerOnly)")
    public void checkManagerRole(JoinPoint joinPoint) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getAuthorities()
                .stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))) {
            throw new ManagerAccessException("MANAGER role required");
        }
    }
}