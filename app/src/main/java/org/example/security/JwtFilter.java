package org.example.security;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final static Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private final static List<String> PUBLIC_PATH_LIST = List.of("/api/public/auth/login");
    private final JwtService jwtService;
    private final AuthenticationEntryPoint authEntryPoint;

    public JwtFilter(JwtService jwtService, AuthenticationEntryPoint authEntryPoint) {
        this.jwtService = jwtService;
        this.authEntryPoint = authEntryPoint;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (PUBLIC_PATH_LIST.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            logger.info(jwt);
            try {
                Long userId = jwtService.parseUserIdFromToken(jwt);
                request.setAttribute("userId", userId);
                filterChain.doFilter(request, response);
                return;
            } catch (AuthenticationCredentialsNotFoundException e) {
                authEntryPoint.commence(request, response, e);
            }
        }
        else {
            response.sendError(401);
        }
    }
}
