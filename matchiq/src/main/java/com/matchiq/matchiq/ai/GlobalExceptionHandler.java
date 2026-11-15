package com.matchiq.matchiq.ai;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AiAnalysisException.class)
    public ResponseEntity<Map<String, String>> handleAiAnalysisException(
            AiAnalysisException exception) {

        Map<String, String> response = Map.of(
                "error",
                "AI analysis is temporarily unavailable. Please try again later."
        );

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
}