package org.example.controllers;

import java.io.IOException;

import org.example.responses.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterExceptionHandler {
    public static Logger logger = LoggerFactory.getLogger(FilterExceptionHandler.class);

    public void handleException(HttpServletResponse response, Exception e, String message, HttpStatus status) throws IOException {
        logger.info("Перехвачено исключение: {}", e.getMessage());

        response.setStatus(401);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(new ErrorResponse(HttpStatus.UNAUTHORIZED, message)));
    }
}
