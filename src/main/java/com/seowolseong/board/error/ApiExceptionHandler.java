package com.seowolseong.board.error;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

/**
 * API 예외 공통 처리 핸들러
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * ApiException 처리
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handle(ApiException e, HttpServletRequest req) {
        ErrorCode c = e.getCode();

        return ResponseEntity
                .status(c.status())
                .body(Map.of(
                        "status", c.status(),
                        "code", c.name(),
                        "message", e.getMessage(),
                        "path", req.getRequestURI()
                ));
    }
}
