package com.example.demo.exceptions;

public class UserNotFoundException extends RuntimeException {
public UserNotFoundException(String message) {
	super(message);
}
}
