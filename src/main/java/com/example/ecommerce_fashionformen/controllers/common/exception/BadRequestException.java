package com.example.ecommerce_fashionformen.controllers.common.exception;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {

        super(message);
    }
}
