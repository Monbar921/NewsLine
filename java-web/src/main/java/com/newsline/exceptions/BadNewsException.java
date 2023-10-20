package com.newsline.exceptions;

public class BadNewsException extends RuntimeException{
    public BadNewsException(String errorMessage) {
        super(errorMessage);
    }
    public BadNewsException() {
    }
}
