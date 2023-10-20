package com.newsline.exceptions;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String errorMessage) {
        super(errorMessage);
    }
    public UserNotFoundException() {
    }
}
