package com.transistflow.commans.exceptions;

import com.transistflow.commans.dtos.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), request.getRequestURI(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), request.getRequestURI(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGlobal(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), request.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiError> buildErrorResponse(String message, String path, HttpStatus status) {
        ApiError error = new ApiError(
                LocalDateTime.now(),
                status.value(),
                message,
                path
        );
        return new ResponseEntity<>(error, status);
    }

}
