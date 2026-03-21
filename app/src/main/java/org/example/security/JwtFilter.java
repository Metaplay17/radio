package org.example.security;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String autrhHeader = request.getHeader("Authorization");
        if (autrhHeader != null && autrhHeader.startsWith("Bearer ")) {
            String jwt = autrhHeader.substring(7);
            Long userId = jwtService.parseUserIdFromToken(jwt);
            request.setAttribute("userId", userId);
            filterChain.doFilter(request, response);
        }
        else {
            response.sendError(401);
        }
    }
}
