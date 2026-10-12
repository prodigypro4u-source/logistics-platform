package com.safwan.logistics.shipment;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/orders/{orderId}/shipment")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @PostMapping
    public ResponseEntity<ShipmentResponse> create(
            @PathVariable("orderId") Long orderId,
            @Valid @RequestBody CreateShipmentRequest request
    ) {
        ShipmentResponse response =
                shipmentService.create(orderId, request);

        URI location = URI.create(
                "/api/orders/" + orderId + "/shipment"
        );

        return ResponseEntity.created(location).body(response);
    }

@GetMapping
    public ResponseEntity<ShipmentResponse> findByOrderId(
            @PathVariable("orderId") Long orderId
    ) {
        ShipmentResponse response = shipmentService.findByOrderId(orderId);
        return ResponseEntity.ok(response);
    }

}