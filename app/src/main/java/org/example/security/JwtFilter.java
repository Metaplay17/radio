package org.example.security;

import java.io.IOException;
import java.util.List;

import org.example.controllers.FilterExceptionHandler;
import org.example.exceptions.InvalidJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    private final FilterExceptionHandler filterExceptionHandler;
    private final UserDetailsService userDetailsService;

    public JwtFilter(JwtService jwtService, FilterExceptionHandler filterExceptionHandler, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.filterExceptionHandler = filterExceptionHandler;
        this.userDetailsService = userDetailsService;
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
            try {
                String username = jwtService.parseUsernameFromToken(jwt);
                UserDetails user = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
                filterChain.doFilter(request, response);
                return;
            } catch (InvalidJwtException e) {
                filterExceptionHandler.handleException(response, e, e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        }
        else {
            filterExceptionHandler.handleException(response, new InvalidJwtException("Нет токена"), "Нет токена", HttpStatus.UNAUTHORIZED);
        }
    }
}
