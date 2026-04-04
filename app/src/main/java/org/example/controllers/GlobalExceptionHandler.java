package org.example.controllers;

import java.util.stream.Collectors;

import org.example.exceptions.AccessDeniedException;
import org.example.exceptions.ConflictException;
import org.example.exceptions.IncorrectArgumentGivenException;
import org.example.exceptions.NotFoundException;
import org.example.exceptions.UserNotFoundException;
import org.example.responses.ErrorResponse;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IncorrectArgumentGivenException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectArgumentGivenException(IncorrectArgumentGivenException e) {
        return ResponseEntity.status(400).body(new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getAllErrors().stream().map((ObjectError o) -> o.getDefaultMessage()).collect(Collectors.joining(" "));
        return ResponseEntity.status(400).body(new ErrorResponse(HttpStatus.BAD_REQUEST, message.trim()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(HttpMessageNotReadableException e) {
        return ResponseEntity.status(400).body(new ErrorResponse(HttpStatus.BAD_REQUEST, "Проверьте правильность заполнения полей"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity.status(403).body(new ErrorResponse(HttpStatus.FORBIDDEN, e.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity.status(404).body(new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(NotFoundException e) {
        return ResponseEntity.status(404).body(new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException e) {
        return ResponseEntity.status(404).body(new ErrorResponse(HttpStatus.NOT_FOUND, "Запрашиваемый ресурс не найден"));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException e) {
        return ResponseEntity.status(409).body(new ErrorResponse(HttpStatus.CONFLICT, e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.status(500).body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Произошла неожиданная внутренняя ошибка сервера"));
    }
}
