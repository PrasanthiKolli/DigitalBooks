package com.digitalbooks.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	@ResponseStatus(HttpStatus.BAD_REQUEST) 
    @ExceptionHandler(HttpClientErrorException.class)
    public String handleHttpClientErrorException(HttpClientErrorException exception) {
		logger.error(exception.getMessage());
        return exception.getMessage();
    }
	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) 
    @ExceptionHandler(Exception.class)
    public String handleAnyException(Exception exception) {
		logger.error(exception.getMessage());
        return exception.getMessage();
    }

}
