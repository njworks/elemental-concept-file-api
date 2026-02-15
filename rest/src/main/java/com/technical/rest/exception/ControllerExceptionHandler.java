package com.technical.rest.exception;

import com.technical.domain.exception.RateLimiterException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

     @ExceptionHandler(RateLimiterException.class)
     @ResponseStatus(value = HttpStatus.FORBIDDEN)
     public String handleRateLimiterException(RateLimiterException ex) {
          return ex.getMessage();
     }
}
