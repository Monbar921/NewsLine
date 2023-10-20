package com.newsline.exceptions;

public class BadCommentException extends RuntimeException{
    public BadCommentException(String errorMessage) {
        super(errorMessage);
    }
    public BadCommentException() {
    }
}
