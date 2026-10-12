package com.safwan.logistics.shipment;

import java.time.Instant;

public record ShipmentEvent(
        String eventId,
        String shipmentId,
        String status,
        String location,
        Instant eventTime
) {
}