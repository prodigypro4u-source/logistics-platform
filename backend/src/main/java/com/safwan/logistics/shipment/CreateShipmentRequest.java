package com.safwan.logistics.shipment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateShipmentRequest(

        @NotBlank(message = "Carrier is required")
        @Size(max = 100, message = "Carrier must be at most 100 characters")
        String carrier,

        @NotBlank(message = "Tracking number is required")
        @Size(max = 120, message = "Tracking number must be at most 120 characters")
        String trackingNumber

) {
}