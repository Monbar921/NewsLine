package com.newsline.exceptions;

public class NewsNotExistsException extends RuntimeException{
    public NewsNotExistsException(String errorMessage) {
        super(errorMessage);
    }
    public NewsNotExistsException() {
    }
}
