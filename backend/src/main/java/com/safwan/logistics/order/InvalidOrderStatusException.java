package com.safwan.logistics.order;

public class InvalidOrderStatusException extends RuntimeException {

    public InvalidOrderStatusException(
            OrderStatus current,
            OrderStatus requested
    ) {
        super("Cannot change order status from "
                + current + " to " + requested);
    }
}