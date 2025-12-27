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

  
	    @ExceptionHandler(ApiException.class)
	    public ResponseEntity<?> handle(ApiException e, HttpServletRequest req) {
	        ErrorCode c = e.getCode();
	        return ResponseEntity.status(c.status()).body(Map.of(
	                "status", c.status(),
	                "code", c.name(),
	                "message", e.getMessage(),      // 기본/상세 메시지
	                "path", req.getRequestURI()
	        ));
	    }

	    @ExceptionHandler(Exception.class)
	    public ResponseEntity<?> handleEtc(Exception e, HttpServletRequest req) {
	        ErrorCode c = ErrorCode.INTERNAL_ERROR;


	        e.printStackTrace();

	        return ResponseEntity.status(c.status()).body(Map.of(
	                "status", c.status(),
	                "code", c.name(),
	                "message", c.defaultMessage(),
	                "path", req.getRequestURI()
	        ));
	    }
	}


