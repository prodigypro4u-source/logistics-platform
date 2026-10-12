package com.safwan.logistics.order;

public class InvalidOrderQueryException extends RuntimeException {

    public InvalidOrderQueryException(String message) {
        super(message);
    }
}