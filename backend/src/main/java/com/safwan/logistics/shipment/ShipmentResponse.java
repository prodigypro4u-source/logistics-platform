package com.safwan.logistics.shipment;

import java.time.Instant;

public record ShipmentResponse(
        Long id,
        Long orderId,
        String carrier,
        String trackingNumber,
        Instant createdAt
) {

    public static ShipmentResponse from(Shipment shipment) {
        return new ShipmentResponse(
                shipment.getId(),
                shipment.getOrder().getId(),
                shipment.getCarrier(),
                shipment.getTrackingNumber(),
                shipment.getCreatedAt()
        );
    }
}