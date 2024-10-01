package com.example.demo.config;

import javax.security.sasl.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.demo.dto.response.ResponseDTO;
import com.example.demo.exceptions.UserNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<Object> handleException(AuthenticationException ex) {
		ResponseDTO response = new ResponseDTO();
		response.setMessage(ex.getMessage());

		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<Object> handleException(UserNotFoundException ex) {
		ResponseDTO response = new ResponseDTO();
		response.setMessage(ex.getMessage());

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
}
