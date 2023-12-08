package com.bookit.bookit.exception;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(String message) {
        super(message);
    }

    // You can also add constructors for different use cases
    // For example, a constructor that also takes a Throwable cause
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
