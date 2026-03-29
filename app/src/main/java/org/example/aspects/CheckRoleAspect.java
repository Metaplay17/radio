package org.example.aspects;

import java.util.List;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.exceptions.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CheckRoleAspect {
    
    @Around("@annotation(CheckRole)")
    public Object checkRole(ProceedingJoinPoint jp, List<String> roles) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.getAuthorities().stream().anyMatch((GrantedAuthority a) -> a.getAuthority().equals("ROLE_CONTENT_MANAGER"))) {
            throw new AccessDeniedException("Доступно только следующим ролям: " + roles.stream().collect(Collectors.joining(", ")));
        }

        return jp.proceed();
    }
}
