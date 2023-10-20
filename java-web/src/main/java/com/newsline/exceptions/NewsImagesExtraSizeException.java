package com.newsline.exceptions;

public class NewsImagesExtraSizeException extends RuntimeException{
    public NewsImagesExtraSizeException(String errorMessage) {
        super(errorMessage);
    }
    public NewsImagesExtraSizeException() {
    }
}
