package com.safwan.logistics.shipment;

public class ShipmentNotFoundException extends RuntimeException {

    public ShipmentNotFoundException(Long orderId) {
        super("Shipment not found for order: " + orderId);
    }
}