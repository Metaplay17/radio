package org.example.aspects;

import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.exceptions.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.lang.Arrays;

@Aspect
@Component
public class CheckRoleAspect {
    
    @Around("@annotation(roles)") // !!!!! ИМЯ СОВПАДАЕТ С ИМЕНЕМ ПАРАМЕТРА !!!!!!!!
    public Object checkRole(ProceedingJoinPoint jp, CheckRole roles) throws Throwable {
        String[] requiredRoles = roles.roles();
        if (requiredRoles.length == 0) {
            return jp.proceed();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.getAuthorities().stream().anyMatch((GrantedAuthority a) -> Arrays.asList(requiredRoles).contains(a.getAuthority()))) {
            throw new AccessDeniedException("Доступно только следующим ролям: " + Arrays.asList(requiredRoles).stream().collect(Collectors.joining(", ")));
        }

        return jp.proceed();
    }
}
