package com.safwan.logistics.order;

import java.time.Instant;

public record OrderResponse(
        Long id,
        String customerName,
        String deliveryAddress,
        OrderStatus status,
        Instant createdAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getDeliveryAddress(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}